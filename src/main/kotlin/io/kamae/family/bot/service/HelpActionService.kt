package io.kamae.family.bot.service

import io.kamae.family.bot.domain.telegram.TelegramActionResult
import io.kamae.family.bot.domain.telegram.dto.TelegramAction
import io.kamae.family.bot.domain.telegram.enums.TelegramBotCommand
import io.kamae.family.bot.service.api.ActionService
import io.kamae.family.bot.service.api.ActionService.Companion.prepareResultWithText
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
@PreAuthorize("hasRole('GUEST')")
class HelpActionService : ActionService {

    override fun executeAndGetResult(telegramAction: TelegramAction): TelegramActionResult {
        val msg =
            "Список команд:\n" + TelegramBotCommand.entries.filter { it.desc != null }
                .joinToString("\n") {
                    "- ${it.command} ${it.desc}"
                }

        return prepareResultWithText(msg, telegramAction)
    }
}