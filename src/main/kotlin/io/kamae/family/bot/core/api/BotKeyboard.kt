package io.kamae.family.bot.core.api

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup

interface BotKeyboard {

    fun getKeyboard(): ReplyKeyboardMarkup

}