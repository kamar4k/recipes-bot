package io.kamae.family.bot.domain.telegram.keyboard

import io.kamae.family.bot.core.api.BotKeyboard
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow

object CancellationKeyboard : BotKeyboard {

    private val CANCELLATION_KEYBOARD: ReplyKeyboardMarkup = ReplyKeyboardMarkup.builder()
        .keyboardRow(KeyboardRow(listOf(KeyboardButton("Отмена"))))
        .resizeKeyboard(true)
        .oneTimeKeyboard(true)
        .selective(true)
        .build()

    override fun getKeyboard(): ReplyKeyboardMarkup {
        return CANCELLATION_KEYBOARD
    }
}