package io.kamae.family.bot.purchases.service

import com.vdurmont.emoji.EmojiParser
import io.kamae.family.bot.AbstractTest
import io.kamae.family.bot.common.domain.keyboard.OneColumnKeyboard
import io.kamae.family.bot.core.api.MessageHistoryProvider
import io.kamae.family.bot.core.api.TelegramBotMessageSender
import io.kamae.family.bot.core.domain.model.TelegramActionResult
import io.kamae.family.bot.core.domain.model.TelegramButton
import io.kamae.family.bot.core.domain.model.TelegramResponse
import io.kamae.family.bot.purchases.client.PurchasesServiceClient
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessages
import org.telegram.telegrambots.meta.api.objects.Message

class ListProductsChangesActionServiceTest : AbstractTest() {

    companion object {
        private val PLUS_EMOJI = EmojiParser.parseToUnicode(":heavy_plus_sign:")
        private val MINUS_EMOJI = EmojiParser.parseToUnicode(":heavy_minus_sign:")
    }

    @MockK
    private lateinit var purchasesServiceClient: PurchasesServiceClient

    @MockK
    private lateinit var sender: TelegramBotMessageSender

    @MockK
    private lateinit var messageHistoryProvider: MessageHistoryProvider

    private val messageMock = mockk<Message>()

    @InjectMockKs
    private lateinit var listProductsChangesActionService: ListProductsChangesActionService

    @Test
    fun executeAction_success() {
        every { messageMock.messageId } returns TEST_MSG_ID
        every { purchasesServiceClient.getProductsInfo() } returns GET_PRODUCTS_INFO_RS_DTO
        every { messageHistoryProvider.getHistory(any(), any()) } returns listOf(
            TEST_MSG_HISTORY, TEST_MSG_HISTORY_ANOTHER
        )
        justRun { messageHistoryProvider.removeHistory(any(), any()) }
        justRun { messageHistoryProvider.addToHistory(any(), any(), any()) }
        every { sender.sendMessage(any<DeleteMessages>()) } returns mockk()
        every { sender.sendMessage(any<SendMessage>()) } returns messageMock

        val result = listProductsChangesActionService.executeAction(formAction())

        val expected = TelegramActionResult(
            TelegramResponse(
                "Продукт\t|\tКоличество\t|\tДействие",
                TEST_CHAT_ID,
                OneColumnKeyboard(
                    listOf(
                        TelegramButton(
                            "$TEST_PRODUCT_NAME\t|\tКол-во: $TEST_PRODUCT_CURR_QUANTITY\t|\t$MINUS_EMOJI",
                            "/reduce-product $TEST_PRODUCT_ID $TEST_PRODUCT_CURR_QUANTITY"
                        ),
                        TelegramButton(
                            "$TEST_PRODUCT_NAME_ANOTHER\t|\tКол-во: $TEST_PRODUCT_CURR_QUANTITY_ANOTHER\t|\t$PLUS_EMOJI",
                            "/increase-product $TEST_PRODUCT_ID_ANOTHER"
                        ),
                    )
                )
            )
        )

        assertEquals(expected, result)

        verify { purchasesServiceClient.getProductsInfo() }
        verify { messageHistoryProvider.getHistory(TEST_CHAT_ID, PROD_LIST_HISTORY_CATEGORY) }
        verify { messageHistoryProvider.removeHistory(TEST_CHAT_ID, PROD_LIST_HISTORY_CATEGORY) }
        verify {
            messageHistoryProvider.addToHistory(TEST_CHAT_ID, PROD_LIST_HISTORY_CATEGORY, withArg {
                assertEquals(
                    TEST_MSG_ID, it.messageId
                )
            })
        }
        verify { sender.sendMessage(DeleteMessages(TEST_CHAT_ID.toString(), listOf(TEST_MSG_ID, TEST_MSG_ID_ANOTHER))) }
        verify {
            sender.sendMessage(
                baseMessageBuilder(expected.telegramResponse.text)
                    .replyMarkup(expected.telegramResponse.keyboard!!.getKeyboard())
                    .build()
            )
        }
    }

    @Test
    fun executeAction_integrationError() {
        val sourceError = IllegalArgumentException("error")

        every { purchasesServiceClient.getProductsInfo() } throws sourceError

        val error = assertThrows<IllegalStateException> { listProductsChangesActionService.executeAction(formAction()) }

        assertEquals("Ошибка от внешнего сервиса: ${sourceError.message}", error.message)
    }
}