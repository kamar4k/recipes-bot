package io.kamae.family.bot.purchases.service

import io.kamae.family.bot.AbstractTest
import io.kamae.family.bot.common.domain.keyboard.CancellationKeyboard
import io.kamae.family.bot.core.api.TelegramBotMessageSender
import io.kamae.family.bot.core.domain.model.CommandContext
import io.kamae.family.bot.core.domain.model.TelegramActionResult
import io.kamae.family.bot.core.domain.model.TelegramResponse
import io.kamae.family.bot.purchases.api.ListProductsSender
import io.kamae.family.bot.purchases.client.PurchasesServiceClient
import io.kamae.family.bot.recipes.domain.keyboard.BaseKeyboard
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

class CreateProductActionServiceTest : AbstractTest() {

    companion object {
        private val INPUT_NAME_CTX_ELEMENT = CommandContext.Element(
            CommandContext.Question("INPUT_NAME"),
            CommandContext.Answer(TEST_PRODUCT_NAME)
        )
        private val INPUT_QUANTITY_CTX_ELEMENT = CommandContext.Element(
            CommandContext.Question("INPUT_QUANTITY"),
            CommandContext.Answer(TEST_PRODUCT_CURR_QUANTITY.toString())
        )

        private val CANCEL_CTX_ELEMENT = CommandContext.Element(
            CommandContext.Question("INPUT_QUANTITY"),
            CommandContext.Answer("Отмена")
        )

    }

    @MockK
    private lateinit var purchasesServiceClient: PurchasesServiceClient

    @MockK
    private lateinit var listProductsSender: ListProductsSender

    @MockK
    private lateinit var sender: TelegramBotMessageSender

    @InjectMockKs
    private lateinit var createProductActionService: CreateProductActionService

    @BeforeEach
    fun applyMocks() {
        every { sender.sendMessage(any<SendMessage>()) } returns mockk()
    }

    @Test
    fun executeAction_emptyContext() {
        val result = createProductActionService.executeAction(formAction())

        val expected = TelegramActionResult(
            TelegramResponse("Введите наименование продукта", TEST_CHAT_ID, CancellationKeyboard),
            CommandContext.Question("INPUT_NAME")
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
    fun executeAction_nameContext() {
        val inputAction = formActionWithContext(context = listOf(INPUT_NAME_CTX_ELEMENT))

        val result = createProductActionService.executeAction(inputAction)

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
        justRun { purchasesServiceClient.createProduct(any()) }
        every { listProductsSender.getAndPushProductsList(any()) } returns mockk()

        val inputAction = formActionWithContext(context = listOf(INPUT_NAME_CTX_ELEMENT, INPUT_QUANTITY_CTX_ELEMENT))

        val result = createProductActionService.executeAction(inputAction)

        val expected = TelegramActionResult(TelegramResponse("Продукт успешно добавлен", TEST_CHAT_ID, BaseKeyboard))

        assertEquals(expected, result)

        verifyAll {
            purchasesServiceClient.createProduct(CREATE_PRODUCT_DTO)
            listProductsSender.getAndPushProductsList(TEST_CHAT_ID)
            sender.sendMessage(
                baseMessageBuilder(expected.telegramResponse.text)
                    .replyMarkup(BaseKeyboard.getKeyboard())
                    .build()
            )
        }
    }

    @Test
    fun executeAction_cancel() {
        val inputAction = formActionWithContext(context = listOf(INPUT_NAME_CTX_ELEMENT, CANCEL_CTX_ELEMENT))

        val result = createProductActionService.executeAction(inputAction)

        val expected = TelegramActionResult(TelegramResponse("Создание продукта отменено", TEST_CHAT_ID, BaseKeyboard))

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
    fun executeAction_incorrectQuantity() {
        val incValue = "qwerty"
        val incDoubleCtx = INPUT_QUANTITY_CTX_ELEMENT.copy(answer = CommandContext.Answer(incValue))
        val inputAction = formActionWithContext(context = listOf(INPUT_NAME_CTX_ELEMENT, incDoubleCtx))

        val result = createProductActionService.executeAction(inputAction)

        val expected = TelegramActionResult(
            TelegramResponse("Ожидается ввод числа. Добавление прервано", TEST_CHAT_ID)
        )

        assertEquals(expected, result)

        verify {
            sender.sendMessage(
                baseMessageBuilder(expected.telegramResponse.text).build()
            )
        }
    }

    @Test
    fun executeAction_clientError() {
        val error = IllegalStateException("text")
        every { purchasesServiceClient.createProduct(any()) } throws error

        val inputAction = formActionWithContext(context = listOf(INPUT_NAME_CTX_ELEMENT, INPUT_QUANTITY_CTX_ELEMENT))

        val result = createProductActionService.executeAction(inputAction)

        val expected = TelegramActionResult(
            TelegramResponse("Неизвестная ошибка: ${error.message}", TEST_CHAT_ID, BaseKeyboard)
        )

        assertEquals(expected, result)

        verify {
            sender.sendMessage(
                baseMessageBuilder(expected.telegramResponse.text)
                    .replyMarkup(BaseKeyboard.getKeyboard())
                    .build()
            )
        }
    }
}