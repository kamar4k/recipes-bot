package io.kamae.family.bot.domain.telegram.dto

data class TelegramResponse(
    val text: String,
    var chatId: Long,
    val buttons: List<TelegramButton>? = null
)