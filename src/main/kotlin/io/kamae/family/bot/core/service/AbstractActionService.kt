package io.kamae.family.bot.core.service

import arrow.core.Either
import io.kamae.family.bot.core.api.ActionService
import io.kamae.family.bot.core.api.TelegramBotMessageSender
import io.kamae.family.bot.core.domain.model.TelegramActionResult
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import java.util.*

abstract class AbstractActionService(
    protected val sender: TelegramBotMessageSender
) : ActionService {
    protected fun sendResult(result: TelegramActionResult): Int {
        val response = result.telegramResponse

        val sendMessage = SendMessage(response.chatId.toString(), response.text)

        if (response.keyboard != null) {
            sendMessage.replyMarkup = response.keyboard.getKeyboard()
        }

        return sender.sendMessage(sendMessage).messageId!!
    }

    protected fun withUUIDCheck(
        strUuid: String,
        onSuccess: (uuid: UUID) -> TelegramActionResult
    ): TelegramActionResult {
        val parsedUUID = Either.catch { UUID.fromString(strUuid) }

        return parsedUUID.fold(
            {
                error("Некорректный идентификатор ($strUuid). Требуется идентификатор формата UUID")
            },
            {
                onSuccess.invoke(it)
            }
        )
    }
}