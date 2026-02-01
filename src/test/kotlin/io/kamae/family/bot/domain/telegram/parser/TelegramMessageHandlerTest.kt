package io.kamae.family.bot.domain.telegram.parser

import io.kamae.family.bot.AbstractTest
import io.kamae.family.bot.domain.telegram.CommandContext
import io.kamae.family.bot.domain.telegram.dto.TelegramResponse
import io.kamae.family.bot.util.exception.TelegramException
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class TelegramMessageHandlerTest : AbstractTest() {
    companion object {
        private const val MISSING_TEXT_MSG = "Отсутствует текстовое сообщение"
    }

    private val telegramMessageHandler = TelegramRecipesMessageHandler()

    @ParameterizedTest
    @MethodSource("parseMessageSuccessCases")
    fun parseTelegramMessage_success(message: String, expectedRequest: CommandContext) {
        val result = telegramMessageHandler.parseMessageAndGetContext(message, TEST_CHAT_ID)

        assertEquals(expectedRequest, result)
    }

    @ParameterizedTest
    @MethodSource("parseMessageErrorCases")
    fun parseTelegramMessage_error(message: String?, expectedResponse: TelegramResponse) {
        val error = assertThrows<TelegramException> {
            telegramMessageHandler.parseMessageAndGetContext(message, TEST_CHAT_ID)
        }

        assertEquals(expectedResponse, error.telegramResponse)
    }

    private fun parseMessageSuccessCases(): List<Arguments> = listOf(
        Arguments.of("/command", CommandContext("/command", null)),
        Arguments.of("/some-command", CommandContext("/some-command", null)),
        Arguments.of("/some-command get recipe", CommandContext("/some-command", "get recipe")),
        Arguments.of("Помощь", CommandContext("/help", null)),
        Arguments.of("Непонятная команда", CommandContext("/default", null))
    )

    private fun parseMessageErrorCases(): List<Arguments> = listOf(
        Arguments.of("", TelegramResponse(MISSING_TEXT_MSG, TEST_CHAT_ID)),
        Arguments.of(null, TelegramResponse(MISSING_TEXT_MSG, TEST_CHAT_ID))
    )
}