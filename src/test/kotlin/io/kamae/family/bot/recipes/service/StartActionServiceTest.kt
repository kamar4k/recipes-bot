package io.kamae.family.bot.recipes.service

import io.kamae.family.bot.AbstractTest
import io.kamae.family.bot.core.service.StartActionService
import io.kamae.family.bot.recipes.domain.keyboard.BaseKeyboard
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class StartActionServiceTest: AbstractTest() {

    private val startActionService: StartActionService = StartActionService()

    @Test
    fun executeAndGetResult_success() {
        val result = startActionService.executeAndGetResult(formAction())

        assertNull(result.nextQuestion)
        assertEquals(
            "Введите команду или нажмите на кнопку",
            result.telegramResponse.text
        )
        assertEquals(BaseKeyboard,result.telegramResponse.keyboard)
    }
}