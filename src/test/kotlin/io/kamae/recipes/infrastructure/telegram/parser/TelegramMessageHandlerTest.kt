package io.kamae.recipes.infrastructure.telegram.parser

import io.kamae.recipes.AbstractTest
import io.kamae.recipes.infrastructure.telegram.dto.TelegramParsedRequest
import io.kamae.recipes.infrastructure.telegram.dto.TelegramResponse
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class TelegramMessageHandlerTest : AbstractTest() {
    companion object {
        private const val INCORRECT_COMMAND_MSG = "Неверный формат команды, подробнее в /help"
        private const val MISSING_TEXT_MSG = "Отсутствует текстовое сообщение"
    }

    private val telegramMessageHandler = TelegramMessageHandler()

    @Test
    fun parseRecipe_success() {
        val requestText = getTestResourcesAsString("correctRequest").replace("\r\n", "\n")

        val result = telegramMessageHandler.parseRecipe(requestText)

        assertEquals(TEST_RECIPE_DTO, result)
    }

    @Test
    fun generateRecipeMessage_success() {
        val result = telegramMessageHandler.generateRecipeMessage(TEST_RECIPE_DTO_WITH_ID)

        val expectedResult = getTestResourcesAsString("correctResponse").replace("\r\n", "\n")

        assertEquals(expectedResult, result)
    }

    @ParameterizedTest
    @MethodSource("parseMessageSuccessCases")
    fun parseTelegramMessage_success(message: String, expectedRequest: TelegramParsedRequest) {
        val result = telegramMessageHandler.parseTelegramMessage(message)

        assertTrue(result.isRight())
        assertEquals(expectedRequest, result.getOrNull())
    }

    @ParameterizedTest
    @MethodSource("parseMessageErrorCases")
    fun parseTelegramMessage_error(message: String?, expectedResponse: TelegramResponse) {
        val result = telegramMessageHandler.parseTelegramMessage(message)

        assertTrue(result.isLeft())
        assertEquals(expectedResponse, result.leftOrNull())
    }

    private fun parseMessageSuccessCases(): List<Arguments> = listOf(
        Arguments.of("/command", TelegramParsedRequest("/command", null)),
        Arguments.of("/some-command", TelegramParsedRequest("/some-command", null)),
        Arguments.of("/some-command get recipe", TelegramParsedRequest("/some-command", "get recipe"))
    )

    private fun parseMessageErrorCases(): List<Arguments> = listOf(
        Arguments.of("/command-123", TelegramResponse(INCORRECT_COMMAND_MSG)),
        Arguments.of("/command ", TelegramResponse(INCORRECT_COMMAND_MSG)),
        Arguments.of("command", TelegramResponse(INCORRECT_COMMAND_MSG)),
        Arguments.of("/", TelegramResponse(INCORRECT_COMMAND_MSG)),
        Arguments.of("", TelegramResponse(MISSING_TEXT_MSG)),
        Arguments.of(null, TelegramResponse(MISSING_TEXT_MSG))
    )
}