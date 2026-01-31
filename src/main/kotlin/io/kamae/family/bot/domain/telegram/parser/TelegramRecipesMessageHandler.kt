package io.kamae.family.bot.domain.telegram.parser

import io.kamae.family.bot.domain.telegram.CommandContext
import io.kamae.family.bot.domain.telegram.dto.TelegramResponse
import io.kamae.family.bot.util.exception.TelegramException
import org.springframework.stereotype.Component

@Component
class TelegramRecipesMessageHandler {

    companion object {
        private const val MESSAGE_PATTERN = "(?s)^/[a-z\\-]+((\\s.+)|$|(\\n.+))"
        private const val COMMAND_PATTERN = "^/[a-z\\-]+(?=\\s|$|\\n)"

        private const val INCORRECT_COMMAND_MSG = "Неверный формат команды, подробнее в /help"
        private const val MISSING_TEXT_MSG = "Отсутствует текстовое сообщение"
    }

    fun parseMessageAndGetContext(text: String?, chatId: Long): CommandContext {
            if (text.isNullOrBlank()) {
                throw TelegramException(TelegramResponse(MISSING_TEXT_MSG, chatId))
            } else {
                if (!text.matches(Regex(MESSAGE_PATTERN))) {
                    throw TelegramException(TelegramResponse(INCORRECT_COMMAND_MSG, chatId))
                } else {
                    val matchedCommand = Regex(COMMAND_PATTERN).find(text)!!

                    val command = matchedCommand.value
                    val data = if (command.length == text.length) {
                        null
                    } else {
                        text.substring(matchedCommand.range.last + 2)
                    }

                    return CommandContext(command, data)
                }
            }
    }
}