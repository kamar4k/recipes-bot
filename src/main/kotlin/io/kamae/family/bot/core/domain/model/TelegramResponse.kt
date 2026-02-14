package io.kamae.family.bot.core.domain.model

import io.kamae.family.bot.core.api.BotKeyboard

data class TelegramResponse(
    val text: String,
    var chatId: Long,
    val keyboard: BotKeyboard? = null
)