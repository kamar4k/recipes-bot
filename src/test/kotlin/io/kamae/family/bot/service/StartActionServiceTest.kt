package io.kamae.family.bot.service

import io.kamae.family.bot.AbstractTest
import io.kamae.family.bot.domain.telegram.keyboard.BaseKeyboard
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

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