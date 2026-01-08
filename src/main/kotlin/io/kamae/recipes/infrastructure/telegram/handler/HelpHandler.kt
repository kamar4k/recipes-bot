package io.kamae.recipes.infrastructure.telegram.handler

import io.kamae.recipes.infrastructure.telegram.dto.TelegramResponse
import io.kamae.recipes.infrastructure.telegram.enums.TelegramBotCommand
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Component

@Component
@PreAuthorize("hasRole('GUEST')")
class HelpHandler : TelegramBotHandler {
    override fun executeCommand(text: String?, chatId: Long): TelegramResponse {
        val msg =
            "Список команд:\n" + TelegramBotCommand.entries.filter { it.desc != null }
                .joinToString("\n") {
                    "- ${it.command} ${it.desc}"
                }

        return TelegramResponse(msg, chatId)
    }
}