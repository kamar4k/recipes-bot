package io.kamae.family.bot.core.service

import io.kamae.family.bot.recipes.domain.keyboard.BaseKeyboard
import io.mockk.impl.annotations.InjectMockKs
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test

class StartActionServiceTest: AbstractDefaultActionServiceTest() {
    @InjectMockKs
    private lateinit var startActionService: StartActionService

    @Test
    fun executeAndGetResult_success() {
        val result = startActionService.executeAction(formAction())

        val expectedMessage = "Введите команду или нажмите на кнопку"
        assertNull(result.nextQuestion)
        assertEquals(
            expectedMessage,
            result.telegramResponse.text
        )
        assertEquals(BaseKeyboard,result.telegramResponse.keyboard)

        verifySenderBase(expectedMessage)
    }
}