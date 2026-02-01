package io.kamae.family.bot.domain.telegram.keyboard

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup

interface BotKeyboard {

    fun getKeyboard(): ReplyKeyboardMarkup

}