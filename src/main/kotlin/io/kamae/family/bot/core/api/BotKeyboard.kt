package io.kamae.family.bot.core.api

import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard

interface BotKeyboard {

    fun getKeyboard(): ReplyKeyboard

}