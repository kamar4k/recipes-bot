package io.kamae.recipes.infrastructure.telegram.handler

import io.kamae.recipes.AbstractTest
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

class HelpHandlerTest : AbstractTest() {
    private val helpHandler: HelpHandler = HelpHandler()

    @Test
    fun executeCommand_success() {
        val result = helpHandler.executeCommand(null)

        val expectedMessage =
            "Список команд:\n" +
                    "- /add Добавление рецепта. /add Наименование\nИнгридиенты(каждый с новой строки)\n\nИнструкции\n" +
                    "- /get Получение рецепта. /get <идентификатор рецепта>\n" +
                    "- /list Список рецептов\n" +
                    "- /help Список команд"

        assertEquals(expectedMessage, result.text)
    }
}