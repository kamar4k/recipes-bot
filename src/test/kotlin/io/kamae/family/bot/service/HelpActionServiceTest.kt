package io.kamae.family.bot.service

import io.kamae.family.bot.AbstractTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class HelpActionServiceTest : AbstractTest() {
    private val helpActionService: HelpActionService = HelpActionService()

    @Test
    fun executeCommand_success() {
        val result = helpActionService.executeAndGetResponse(formAction("/help"))

        val expectedMessage =
            "Список команд:\n" +
                    "- /add Добавление рецепта. /add Наименование\nИнгридиенты(каждый с новой строки)\n\nИнструкции\n" +
                    "- /get Получение рецепта. /get <идентификатор рецепта>\n" +
                    "- /list Список рецептов\n" +
                    "- /help Список команд"

        assertEquals(expectedMessage, result.text)
    }
}