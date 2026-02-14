package io.kamae.family.bot.core.service

import io.kamae.family.bot.core.api.ActionService.Companion.prepareResultWithText
import io.kamae.family.bot.core.api.TelegramBotMessageSender
import io.kamae.family.bot.core.domain.model.TelegramAction
import io.kamae.family.bot.core.domain.model.TelegramActionResult
import io.kamae.family.bot.core.factory.CommandSetFactory
import org.springframework.stereotype.Service

@Service
class HelpActionService(
    private val commandSetFactory: CommandSetFactory, sender: TelegramBotMessageSender
) : AbstractDefaultActionService(sender) {

    override fun executeAndGetResult(telegramAction: TelegramAction): TelegramActionResult {
        val msg =
            "Список команд:\n" + commandSetFactory.getCommands().filter { it.desc != null }
                .joinToString("\n") {
                    "- ${it.command} ${it.desc}"
                }

        return prepareResultWithText(msg, telegramAction)
    }
}