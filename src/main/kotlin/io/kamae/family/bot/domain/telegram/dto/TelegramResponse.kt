package io.kamae.family.bot.domain.telegram.dto

import io.kamae.family.bot.domain.telegram.keyboard.BotKeyboard

data class TelegramResponse(
    val text: String,
    var chatId: Long,
    val buttons: List<TelegramButton>? = null,
    val keyboard: BotKeyboard? = null
) {
    init {
        if (buttons != null && keyboard != null) error("В ответ может быть добавлена только одна клавиатура")
    }
}