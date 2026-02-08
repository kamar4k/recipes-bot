package io.kamae.family.bot.core.service

import io.kamae.family.bot.core.api.ActionService
import io.kamae.family.bot.core.api.TelegramBotMessageSender
import io.kamae.family.bot.core.domain.model.TelegramAction
import io.kamae.family.bot.core.domain.model.TelegramActionResult
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton

abstract class AbstractDefaultActionService(
    private val telegramBotMessageSender: TelegramBotMessageSender
) : ActionService {

    override fun executeAction(
        telegramAction: TelegramAction
    ): TelegramActionResult {
        val result = executeAndGetResult(telegramAction)
        val response = result.telegramResponse

        val sendMessage = SendMessage(response.chatId.toString(), response.text)

        val buttons = response.buttons?.map {
            val button = InlineKeyboardButton(it.name)
            button.callbackData = it.action
            listOf(button)
        }

        if (buttons != null) {
            sendMessage.replyMarkup = InlineKeyboardMarkup(buttons)
        } else if (response.keyboard != null) {
            sendMessage.replyMarkup = response.keyboard.getKeyboard()
        }

        telegramBotMessageSender.sendMessage(sendMessage)

        return result
    }

    abstract fun executeAndGetResult(telegramAction: TelegramAction): TelegramActionResult
}