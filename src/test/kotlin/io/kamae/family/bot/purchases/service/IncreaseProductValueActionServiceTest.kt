package io.kamae.family.bot.purchases.service

import io.kamae.family.bot.AbstractTest
import io.kamae.family.bot.common.domain.keyboard.CancellationKeyboard
import io.kamae.family.bot.core.api.TelegramBotMessageSender
import io.kamae.family.bot.core.domain.model.CommandContext
import io.kamae.family.bot.core.domain.model.TelegramActionResult
import io.kamae.family.bot.core.domain.model.TelegramResponse
import io.kamae.family.bot.purchases.api.ListProductsSender
import io.kamae.family.bot.purchases.client.PurchasesServiceClient
import io.kamae.family.bot.purchases.client.dto.ChangeType
import io.kamae.family.bot.recipes.domain.keyboard.BaseKeyboard
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message

class IncreaseProductValueActionServiceTest : AbstractTest() {
    @MockK
    private lateinit var purchasesServiceClient: PurchasesServiceClient

    @MockK
    private lateinit var listProductsSender: ListProductsSender

    @MockK
    private lateinit var sender: TelegramBotMessageSender

    private val messageMock = mockk<Message>()

    @InjectMockKs
    private lateinit var increaseProductValueActionService: IncreaseProductValueActionService

    companion object {
        private val INPUT_QUANTITY_CTX_ELEMENT = CommandContext.Element(
            CommandContext.Question("INPUT_QUANTITY"),
            CommandContext.Answer(TEST_PRODUCT_CURR_QUANTITY.toString())
        )

        private val CANCEL_CTX_ELEMENT = CommandContext.Element(
            CommandContext.Question("INPUT_QUANTITY"),
            CommandContext.Answer("Отмена")
        )
    }

    @BeforeEach
    fun applyMocks() {
        every { messageMock.messageId } returns TEST_MSG_ID
        every { sender.sendMessage(any<SendMessage>()) } returns messageMock
    }

    @Test
    fun executeAction_emptyContext() {
        val result = increaseProductValueActionService.executeAction(
            formAction("/increase-product", TEST_PRODUCT_ID.toString())
        )

        val expected = TelegramActionResult(
            TelegramResponse("Введите количество", TEST_CHAT_ID, CancellationKeyboard),
            CommandContext.Question("INPUT_QUANTITY")
        )

        assertEquals(expected, result)
        verify {
            sender.sendMessage(
                baseMessageBuilder(expected.telegramResponse.text)
                    .replyMarkup(CancellationKeyboard.getKeyboard())
                    .build()
            )
        }
    }

    @Test
    fun executeAction_fullContext() {
        justRun { purchasesServiceClient.addProductEvent(any(), any()) }
        every { listProductsSender.getAndPushProductsList(any()) } returns mockk()

        val inputAction = formActionWithContext(
            "/increase-product", TEST_PRODUCT_ID.toString(), listOf(INPUT_QUANTITY_CTX_ELEMENT)
        )

        val result = increaseProductValueActionService.executeAction(inputAction)

        val expected = TelegramActionResult(
            TelegramResponse("Изменение продукта выполнено", TEST_CHAT_ID, BaseKeyboard)
        )

        assertEquals(expected, result)
        verify {
            purchasesServiceClient.addProductEvent(
                ADD_EVENT_DTO.copy(ADD_EVENT_DTO.event.copy(changeType = ChangeType.INCREASE)),
                TEST_PRODUCT_ID
            )
        }

        verify { listProductsSender.getAndPushProductsList(TEST_CHAT_ID) }

        verify {
            sender.sendMessage(
                baseMessageBuilder(expected.telegramResponse.text)
                    .replyMarkup(BaseKeyboard.getKeyboard())
                    .build()
            )
        }
    }

    @Test
    fun executeAction_cancel() {
        val inputAction = formActionWithContext(context = listOf(CANCEL_CTX_ELEMENT))

        val result = increaseProductValueActionService.executeAction(inputAction)

        val expected = TelegramActionResult(TelegramResponse("Изменение продукта отменено", TEST_CHAT_ID, BaseKeyboard))

        assertEquals(expected, result)
        verify {
            sender.sendMessage(
                baseMessageBuilder(expected.telegramResponse.text)
                    .replyMarkup(BaseKeyboard.getKeyboard())
                    .build()
            )
        }
    }

    @Test
    fun executeAction_incUUID() {
        val inputAction = formActionWithContext(text = "1234", context = listOf(INPUT_QUANTITY_CTX_ELEMENT))

        val error = assertThrows<IllegalStateException> { increaseProductValueActionService.executeAction(inputAction) }

        assertEquals(
            "Некорректный идентификатор (${inputAction.commandContext.text}). Требуется идентификатор формата UUID",
            error.message
        )
    }

    @Test
    fun executeAction_incQuantity() {
        every { listProductsSender.getAndPushProductsList(any()) } returns mockk()

        val inputAction = formActionWithContext(
            text = TEST_PRODUCT_ID.toString(),
            context = listOf(INPUT_QUANTITY_CTX_ELEMENT.copy(answer = CommandContext.Answer("qwerty")))
        )

        val error = assertThrows<IllegalStateException> { increaseProductValueActionService.executeAction(inputAction) }

        assertEquals("Некорректное значение количества: qwerty", error.message)
    }

    @Test
    fun executeAction_integrationError() {
        val sourceError = IllegalStateException("error")

        every { purchasesServiceClient.addProductEvent(any(), any()) } throws sourceError

        val inputAction = formActionWithContext(
            "/increase-product", TEST_PRODUCT_ID.toString(), listOf(INPUT_QUANTITY_CTX_ELEMENT)
        )

        val error = assertThrows<IllegalStateException> { increaseProductValueActionService.executeAction(inputAction) }

        assertEquals("Ошибка от внешнего сервиса: ${sourceError.message}", error.message)

        verify {
            purchasesServiceClient.addProductEvent(
                ADD_EVENT_DTO.copy(ADD_EVENT_DTO.event.copy(changeType = ChangeType.INCREASE)),
                TEST_PRODUCT_ID
            )
        }

    }
}