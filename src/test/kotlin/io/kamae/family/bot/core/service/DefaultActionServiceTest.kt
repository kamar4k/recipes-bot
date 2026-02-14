package io.kamae.family.bot.core.service

import io.mockk.impl.annotations.InjectMockKs
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNull

class DefaultActionServiceTest : AbstractDefaultActionServiceTest() {

    @InjectMockKs
    private lateinit var defaultActionService: DefaultActionService

    @Test
    fun executeCommand_success() {
        val result = defaultActionService.executeAction(formAction())

        assertNull(result.nextQuestion)
        val expectedMessage = "Я Вас не понял. Для получения списка возможных команд введите /help"
        assertEquals(
            expectedMessage,
            result.telegramResponse.text
        )

        verifySenderOnlyMessage(expectedMessage)
    }
}