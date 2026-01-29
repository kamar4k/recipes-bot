package io.kamae.family.bot.domain.telegram.parser

import io.kamae.family.bot.AbstractTest
import io.kamae.family.bot.domain.telegram.dto.TelegramParsedRequest
import io.kamae.family.bot.domain.telegram.dto.TelegramResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class TelegramMessageHandlerTest : AbstractTest() {
    companion object {
        private const val INCORRECT_COMMAND_MSG = "Неверный формат команды, подробнее в /help"
        private const val MISSING_TEXT_MSG = "Отсутствует текстовое сообщение"
    }

    private val telegramMessageHandler = TelegramRecipesMessageHandler()

    @ParameterizedTest
    @MethodSource("parseMessageSuccessCases")
    fun parseTelegramMessage_success(message: String, expectedRequest: TelegramParsedRequest) {
        val result = telegramMessageHandler.parseTelegramMessage(message, TEST_CHAT_ID)

        assertTrue(result.isRight())
        assertEquals(expectedRequest, result.getOrNull())
    }

    @ParameterizedTest
    @MethodSource("parseMessageErrorCases")
    fun parseTelegramMessage_error(message: String?, expectedResponse: TelegramResponse) {
        val result = telegramMessageHandler.parseTelegramMessage(message, TEST_CHAT_ID)

        assertTrue(result.isLeft())
        assertEquals(expectedResponse, result.leftOrNull())
    }

    private fun parseMessageSuccessCases(): List<Arguments> = listOf(
        Arguments.of("/command", TelegramParsedRequest("/command", null)),
        Arguments.of("/some-command", TelegramParsedRequest("/some-command", null)),
        Arguments.of("/some-command get recipe", TelegramParsedRequest("/some-command", "get recipe"))
    )

    private fun parseMessageErrorCases(): List<Arguments> = listOf(
        Arguments.of("/command-123", TelegramResponse(INCORRECT_COMMAND_MSG, TEST_CHAT_ID)),
        Arguments.of("/command ", TelegramResponse(INCORRECT_COMMAND_MSG, TEST_CHAT_ID)),
        Arguments.of("command", TelegramResponse(INCORRECT_COMMAND_MSG, TEST_CHAT_ID)),
        Arguments.of("/", TelegramResponse(INCORRECT_COMMAND_MSG, TEST_CHAT_ID)),
        Arguments.of("", TelegramResponse(MISSING_TEXT_MSG, TEST_CHAT_ID)),
        Arguments.of(null, TelegramResponse(MISSING_TEXT_MSG, TEST_CHAT_ID))
    )
}