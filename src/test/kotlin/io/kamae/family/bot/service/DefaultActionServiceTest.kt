package io.kamae.family.bot.service

import io.kamae.family.bot.AbstractTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class DefaultActionServiceTest: AbstractTest() {

    private val defaultActionService: DefaultActionService = DefaultActionService()

    @Test
    fun executeCommand_success() {
        val result = defaultActionService.executeAndGetResponse(formAction())

        assertEquals("Я Вас не понял. Для получения списка возможных команд введите /help", result.text)
    }
}