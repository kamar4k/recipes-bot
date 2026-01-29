package io.kamae.family.bot.listener

import io.kamae.family.bot.config.TelegramBotConfig
import io.kamae.family.bot.domain.telegram.dto.TelegramResponse
import io.kamae.family.bot.listener.delegate.TelegramBotDelegate
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton

@Component
class RecipesBot(
    private val telegramBotConfig: TelegramBotConfig,
    private val telegramBotDelegate: TelegramBotDelegate
) : TelegramLongPollingBot(telegramBotConfig.token) {
    override fun getBotUsername(): String = telegramBotConfig.name
    override fun onUpdateReceived(update: Update?) {
        if (update == null) return

        val response = telegramBotDelegate.processUpdate(update)

        sendMessage(response)
    }

    private fun sendMessage(response: TelegramResponse) {
        val sendMessage = SendMessage(response.chatId.toString(), response.text)

        val buttons = response.buttons?.map {
            val button = InlineKeyboardButton(it.name)
            button.callbackData = it.action
            listOf(button)
        }

        if (buttons != null) {
            sendMessage.replyMarkup = InlineKeyboardMarkup(buttons)
        }

        execute(sendMessage)
    }
}