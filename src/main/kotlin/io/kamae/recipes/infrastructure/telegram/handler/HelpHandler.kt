package io.kamae.recipes.infrastructure.telegram.handler

import io.kamae.recipes.infrastructure.telegram.dto.TelegramResponse
import io.kamae.recipes.infrastructure.telegram.enums.TelegramBotCommand
import org.springframework.stereotype.Component

@Component
class HelpHandler : TelegramBotHandler {
    override fun executeCommand(text: String?): TelegramResponse {
        var msg =
            "Список команд:\n" + TelegramBotCommand.entries.filter { it.desc != null }
                .joinToString("\n") {
                    "- ${it.command} ${it.desc}"
                }

        return TelegramResponse(msg)
    }
}