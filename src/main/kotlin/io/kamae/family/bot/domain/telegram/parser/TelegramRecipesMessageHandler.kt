package io.kamae.family.bot.domain.telegram.parser

import arrow.core.Either
import io.kamae.family.bot.domain.telegram.dto.TelegramParsedRequest
import io.kamae.family.bot.domain.telegram.dto.TelegramResponse
import org.springframework.stereotype.Component

@Component
class TelegramRecipesMessageHandler {

    companion object {
        private const val MESSAGE_PATTERN = "(?s)^/[a-z\\-]+((\\s.+)|$|(\\n.+))"
        private const val COMMAND_PATTERN = "^/[a-z\\-]+(?=\\s|$|\\n)"

        private const val INCORRECT_COMMAND_MSG = "Неверный формат команды, подробнее в /help"
        private const val MISSING_TEXT_MSG = "Отсутствует текстовое сообщение"
    }

    fun parseTelegramMessage(text: String?, chatId: Long): Either<TelegramResponse, TelegramParsedRequest> {
            if (text.isNullOrBlank()) {
                return Either.Left(TelegramResponse(MISSING_TEXT_MSG, chatId))
            } else {
                if (!text.matches(Regex(MESSAGE_PATTERN))) {
                    return Either.Left(TelegramResponse(INCORRECT_COMMAND_MSG, chatId))
                } else {
                    val matchedCommand = Regex(COMMAND_PATTERN).find(text)!!

                    val command = matchedCommand.value
                    val data = if (command.length == text.length) {
                        null
                    } else {
                        text.substring(matchedCommand.range.last + 2)
                    }

                    return Either.Right(TelegramParsedRequest(command, data))
                }
            }
    }
}