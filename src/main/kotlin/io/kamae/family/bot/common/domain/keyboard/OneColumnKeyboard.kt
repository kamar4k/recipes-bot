package io.kamae.family.bot.common.domain.keyboard

import io.kamae.family.bot.core.api.BotKeyboard
import io.kamae.family.bot.core.domain.model.TelegramButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton

data class OneColumnKeyboard(private val buttons: List<TelegramButton>) : BotKeyboard {
    override fun getKeyboard(): ReplyKeyboard {
        val column = buttons.map {
            val button = InlineKeyboardButton(it.name)
            button.callbackData = it.action
            listOf(button)
        }

        return InlineKeyboardMarkup(column)
    }
}