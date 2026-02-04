package io.kamae.family.bot.core.domain.model

import io.kamae.family.bot.core.api.BotKeyboard

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