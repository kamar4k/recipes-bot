package io.kamae.recipes.infrastructure.telegram.handler

import io.kamae.recipes.AbstractTest
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

class DefaultHandlerTest: AbstractTest() {

    private val defaultHandler: DefaultHandler = DefaultHandler()

    @Test
    fun executeCommand_success() {
        val result = defaultHandler.executeCommand(TELEGRAM_COMMAND_TEXT)

        assertEquals("Я Вас не понял. Для получения списка возможных команд введите /help", result.text)
    }
}