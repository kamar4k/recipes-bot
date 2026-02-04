package io.kamae.family.bot.recipes.service

import io.kamae.family.bot.AbstractTest
import io.kamae.family.bot.core.api.TelegramBotCommand
import io.kamae.family.bot.core.domain.enums.CoreCommand
import io.kamae.family.bot.core.factory.CommandSetFactory
import io.kamae.family.bot.core.service.HelpActionService
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNull

class HelpActionServiceTest : AbstractTest() {

    private val commandSetFactory = mockk<CommandSetFactory>()

    private val helpActionService: HelpActionService = HelpActionService(commandSetFactory)

    @Test
    fun executeCommand_success() {
        every { commandSetFactory.getCommands() } returns CoreCommand.entries

        val result = helpActionService.executeAndGetResult(formAction("/help"))

        val expectedMessage =
            "Список команд:\n" +
                    "- /start Переход в главное меню\n" +
                    "- /help Список команд"

        assertNull(result.nextQuestion)
        assertEquals(expectedMessage, result.telegramResponse.text)
    }
}