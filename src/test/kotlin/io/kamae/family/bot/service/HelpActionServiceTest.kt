package io.kamae.family.bot.service

import io.kamae.family.bot.AbstractTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNull

class HelpActionServiceTest : AbstractTest() {
    private val helpActionService: HelpActionService = HelpActionService()

    @Test
    fun executeCommand_success() {
        val result = helpActionService.executeAndGetResult(formAction("/help"))

        val expectedMessage =
            "Список команд:\n" +
                    "- /start Переход в главное меню\n" +
                    "- /add Добавление рецепта\n" +
                    "- /get Получение рецепта. /get <идентификатор рецепта>\n" +
                    "- /list Список рецептов\n" +
                    "- /help Список команд"

        assertNull(result.nextQuestion)
        assertEquals(expectedMessage, result.telegramResponse.text)
    }
}