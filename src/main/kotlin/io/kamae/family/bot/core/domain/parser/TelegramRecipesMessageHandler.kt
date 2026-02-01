package io.kamae.family.bot.core.domain.parser

import io.kamae.family.bot.core.domain.model.CommandContext
import io.kamae.family.bot.core.domain.model.TelegramResponse
import io.kamae.family.bot.domain.telegram.enums.RecipesCommand
import io.kamae.family.bot.core.exception.TelegramException
import org.springframework.stereotype.Component

@Component
class TelegramRecipesMessageHandler {

    companion object {
        private const val MESSAGE_PATTERN = "(?s)^/[a-z\\-]+((\\s.+)|$|(\\n.+))"
        private const val COMMAND_PATTERN = "^/[a-z\\-]+(?=\\s|$|\\n)"

        private const val MISSING_TEXT_MSG = "Отсутствует текстовое сообщение"
    }

    fun parseMessageAndGetContext(text: String?, chatId: Long): CommandContext {
        if (text.isNullOrBlank()) {
            throw TelegramException(TelegramResponse(MISSING_TEXT_MSG, chatId))
        } else {
            if (text.matches(Regex(MESSAGE_PATTERN))) {
                val matchedCommand = Regex(COMMAND_PATTERN).find(text)!!

                val command = matchedCommand.value
                val data = if (command.length == text.length) {
                    null
                } else {
                    text.substring(matchedCommand.range.last + 2)
                }

                return CommandContext(command, data)
            } else {
                return CommandContext(RecipesCommand.searchByAlias(text).command, null)
            }
        }
    }
}