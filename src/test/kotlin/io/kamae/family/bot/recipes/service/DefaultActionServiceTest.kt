package io.kamae.family.bot.recipes.service

import io.kamae.family.bot.AbstractTest
import io.kamae.family.bot.core.service.DefaultActionService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNull

class DefaultActionServiceTest : AbstractTest() {

    private val defaultActionService: DefaultActionService = DefaultActionService()

    @Test
    fun executeCommand_success() {
        val result = defaultActionService.executeAndGetResult(formAction())

        assertNull(result.nextQuestion)
        assertEquals(
            "Я Вас не понял. Для получения списка возможных команд введите /help",
            result.telegramResponse.text
        )
    }
}