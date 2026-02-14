package io.kamae.family.bot.purchases.service

import io.kamae.family.bot.AbstractTest
import io.kamae.family.bot.core.api.MessageHistoryProvider
import io.kamae.family.bot.core.api.TelegramBotMessageSender
import io.kamae.family.bot.core.domain.model.TelegramActionResult
import io.kamae.family.bot.core.domain.model.TelegramResponse
import io.kamae.family.bot.purchases.api.ListProductsSender
import io.kamae.family.bot.purchases.client.PurchasesServiceClient
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

class ReduceProductValueActionServiceTest : AbstractTest() {

    @MockK
    private lateinit var purchasesServiceClient: PurchasesServiceClient

    @MockK
    private lateinit var listProductsSender: ListProductsSender

    @MockK
    private lateinit var sender: TelegramBotMessageSender

    @MockK
    private lateinit var messageHistoryProvider: MessageHistoryProvider

    private val messageMock = mockk<Message>()

    @InjectMockKs
    private lateinit var reduceProductValueActionService: ReduceProductValueActionService

    @BeforeEach
    fun applyMocks() {
        every { messageMock.messageId } returns TEST_MSG_ID
        every { sender.sendMessage(any<SendMessage>()) } returns messageMock
        every { listProductsSender.getAndPushProductsList(any()) } returns mockk()
        justRun { messageHistoryProvider.addToHistory(any(), any(), any()) }
    }

    @Test
    fun executeAction_success() {
        justRun { purchasesServiceClient.addProductEvent(any(), any()) }

        val inputAction = formAction("/reduce-product", "$TEST_PRODUCT_ID $TEST_PRODUCT_CURR_QUANTITY")

        val result = reduceProductValueActionService.executeAction(inputAction)

        val expected = TelegramActionResult(
            TelegramResponse("Изменение продукта выполнено", TEST_CHAT_ID)
        )

        assertEquals(expected, result)
        verify { purchasesServiceClient.addProductEvent(ADD_EVENT_DTO, TEST_PRODUCT_ID) }
        verify {
            messageHistoryProvider.addToHistory(
                TEST_CHAT_ID,
                PROD_LIST_HISTORY_CATEGORY,
                withArg { assertEquals(TEST_MSG_ID, it.messageId) }
            )
        }
        verify {
            sender.sendMessage(
                baseMessageBuilder(expected.telegramResponse.text)
                    .build()
            )
        }
        verify { listProductsSender.getAndPushProductsList(TEST_CHAT_ID) }
    }

    @Test
    fun executeAction_incCommand() {
        justRun { purchasesServiceClient.addProductEvent(any(), any()) }

        val inputAction = formAction("/reduce-product", "$TEST_PRODUCT_ID $TEST_PRODUCT_CURR_QUANTITY 123")

        val error = assertThrows<IllegalStateException> { reduceProductValueActionService.executeAction(inputAction) }

        assertEquals("Неверный формат команды", error.message)
    }

    @Test
    fun executeAction_incUUID() {
        val inputAction = formAction("/reduce-product", "1234 $TEST_PRODUCT_CURR_QUANTITY")

        val error = assertThrows<IllegalStateException> { reduceProductValueActionService.executeAction(inputAction) }

        assertEquals("Некорректный идентификатор (1234). Требуется идентификатор формата UUID", error.message)
    }

    @Test
    fun executeAction_incQuantity() {
        val inputAction = formAction("/reduce-product", "$TEST_PRODUCT_ID qwerty")

        val error = assertThrows<IllegalStateException> { reduceProductValueActionService.executeAction(inputAction) }

        assertEquals("Некорректное значение количества: qwerty", error.message)
    }

    @Test
    fun executeAction_integrationError() {
        val sourceError = IllegalArgumentException("error")
        every { purchasesServiceClient.addProductEvent(any(), any()) } throws sourceError

        val inputAction = formAction("/reduce-product", "$TEST_PRODUCT_ID $TEST_PRODUCT_CURR_QUANTITY")

        val error = assertThrows<IllegalStateException> { reduceProductValueActionService.executeAction(inputAction) }

        assertEquals("Ошибка от внешнего сервиса: ${sourceError.message}", error.message)
    }
}