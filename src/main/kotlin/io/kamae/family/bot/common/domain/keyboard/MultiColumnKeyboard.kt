package io.kamae.family.bot.common.domain.keyboard

import io.kamae.family.bot.core.api.BotKeyboard
import io.kamae.family.bot.core.domain.model.TelegramButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton

data class MultiColumnKeyboard(private val rows: List<List<TelegramButton>>) : BotKeyboard {
    override fun getKeyboard(): ReplyKeyboard {
        val keyboardRows = rows.map { row ->
            row.map {
                InlineKeyboardButton.builder()
                    .text(it.name)
                    .callbackData(it.action)
                    .build()
            }
        }

        return InlineKeyboardMarkup(keyboardRows)
    }
}