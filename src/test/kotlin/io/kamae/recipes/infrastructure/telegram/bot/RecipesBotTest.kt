package io.kamae.recipes.infrastructure.telegram.bot

import arrow.core.Either
import com.ninjasquad.springmockk.MockkBean
import com.ninjasquad.springmockk.SpykBean
import io.kamae.recipes.AbstractIntegrationTest
import io.kamae.recipes.infrastructure.telegram.dto.TelegramParsedRequest
import io.kamae.recipes.infrastructure.telegram.dto.TelegramResponse
import io.kamae.recipes.infrastructure.telegram.handler.TelegramBotHandler
import io.kamae.recipes.infrastructure.telegram.handler.factory.TelegramBotHandlerFactory
import io.kamae.recipes.infrastructure.telegram.parser.TelegramMessageHandler
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update

class RecipesBotTest : AbstractIntegrationTest() {

    @SpykBean
    private lateinit var recipesBot: RecipesBot

    @MockkBean
    private lateinit var telegramMessageHandler: TelegramMessageHandler

    @MockkBean
    private lateinit var telegramBotHandlerFactory: TelegramBotHandlerFactory

    private val telegramBotHandler: TelegramBotHandler = mockk<TelegramBotHandler>()

    private val update: Update = mockk<Update>()
    private val message: Message = mockk<Message>()

    @BeforeEach
    fun mockkHandler() {
        every { telegramBotHandlerFactory.getHandler(TELEGRAM_COMMAND_TEXT) } returns telegramBotHandler
        every { update.message } returns message
    }

    @Test
    fun getBotUsername_success() {
        val result = recipesBot.botUsername

        assertEquals("recipes-bot", result)
    }

    @Test
    fun onUpdateReceived_success() {
        every { message.chatId } returns TEST_CHAT_ID
        every { message.text } returns TELEGRAM_MESSAGE_TEXT
        every { telegramMessageHandler.parseTelegramMessage(TELEGRAM_MESSAGE_TEXT) } returns Either.Right(
            TelegramParsedRequest(
                TELEGRAM_COMMAND_TEXT, null
            )
        )
        every { telegramBotHandler.executeCommand(any()) } returns TelegramResponse(
            TELEGRAM_RESPONSE_TEXT
        )

        every { recipesBot.execute(any<SendMessage>()) } returns null

        recipesBot.onUpdateReceived(update)

        verify {
            recipesBot.execute(SendMessage(TEST_CHAT_ID.toString(), TELEGRAM_RESPONSE_TEXT))
        }
    }

    @ParameterizedTest
    @MethodSource("errorTestCases")
    fun onUpdateReceived_commonFail(errorMessage: String?, expectedResponse: String) {
        every { message.chatId } returns TEST_CHAT_ID
        every { message.text } returns TELEGRAM_MESSAGE_TEXT
        every { telegramMessageHandler.parseTelegramMessage(TELEGRAM_MESSAGE_TEXT) } returns Either.Right(
            TelegramParsedRequest(
                TELEGRAM_COMMAND_TEXT, null
            )
        )
        every { telegramBotHandler.executeCommand(any()) } throws RuntimeException(errorMessage)

        every { recipesBot.execute(any<SendMessage>()) } returns null

        recipesBot.onUpdateReceived(update)

        verify {
            recipesBot.execute(SendMessage(TEST_CHAT_ID.toString(), expectedResponse))
        }
    }

    @Test
    fun onUpdateReceived_chatIdNotFound() {
        every { message.chatId } returns null

        val error = assertThrows<IllegalStateException> { recipesBot.onUpdateReceived(update) }

        assertEquals("Не удалось определить chatId", error.message)
    }

    private fun errorTestCases(): List<Arguments> = listOf(
        Arguments.of("error", "error"),
        Arguments.of(null, "Ошибка обработки запроса")
    )

    @Test
    fun onUpdateReceived_failParse() {
        every { message.chatId } returns TEST_CHAT_ID
        every { message.text } returns TELEGRAM_MESSAGE_TEXT
        every { telegramMessageHandler.parseTelegramMessage(TELEGRAM_MESSAGE_TEXT) } returns Either.Left(
            TelegramResponse(
                TELEGRAM_RESPONSE_TEXT
            )
        )

        every { recipesBot.execute(any<SendMessage>()) } returns null

        recipesBot.onUpdateReceived(update)


        verify(exactly = 0) { telegramBotHandlerFactory.getHandler(any()) }
        verify {
            recipesBot.execute(SendMessage(TEST_CHAT_ID.toString(), TELEGRAM_RESPONSE_TEXT))
        }
    }
}