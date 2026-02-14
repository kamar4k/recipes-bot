package io.kamae.family.bot.core.domain.model

data class TelegramUpdateParams(val chatId: Long, val text: String, val messageId: Int? = null)