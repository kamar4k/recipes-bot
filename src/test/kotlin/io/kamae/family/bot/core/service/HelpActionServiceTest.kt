package io.kamae.family.bot.core.service

import io.kamae.family.bot.core.domain.enums.CoreCommand
import io.kamae.family.bot.core.factory.CommandSetFactory
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNull

class HelpActionServiceTest : AbstractDefaultActionServiceTest() {

    @MockK
    private lateinit var commandSetFactory: CommandSetFactory

    @InjectMockKs
    private lateinit var helpActionService: HelpActionService

    @Test
    fun executeCommand_success() {
        every { commandSetFactory.getCommands() } returns CoreCommand.entries

        val result = helpActionService.executeAction(formAction("/help"))

        val expectedMessage =
            "Список команд:\n" +
                    "- /start Переход в главное меню\n" +
                    "- /help Список команд"

        assertNull(result.nextQuestion)
        assertEquals(expectedMessage, result.telegramResponse.text)

        verifySenderOnlyMessage(expectedMessage)
    }
}