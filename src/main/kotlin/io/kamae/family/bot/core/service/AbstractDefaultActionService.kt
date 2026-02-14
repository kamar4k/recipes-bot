package io.kamae.family.bot.core.service

import io.kamae.family.bot.core.api.TelegramBotMessageSender
import io.kamae.family.bot.core.domain.model.TelegramAction
import io.kamae.family.bot.core.domain.model.TelegramActionResult
import org.telegram.telegrambots.meta.api.methods.send.SendMessage

abstract class AbstractDefaultActionService(
    sender: TelegramBotMessageSender
) : AbstractActionService(sender) {

    override fun executeAction(
        telegramAction: TelegramAction
    ): TelegramActionResult {
        val result = executeAndGetResult(telegramAction)
        val response = result.telegramResponse

        val sendMessage = SendMessage(response.chatId.toString(), response.text)

        if (response.keyboard != null) {
            sendMessage.replyMarkup = response.keyboard.getKeyboard()
        }

        sender.sendMessage(sendMessage)

        return result
    }

    abstract fun executeAndGetResult(telegramAction: TelegramAction): TelegramActionResult
}