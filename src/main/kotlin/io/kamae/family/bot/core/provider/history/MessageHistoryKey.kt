package io.kamae.family.bot.core.provider.history

import io.kamae.family.bot.core.api.model.MessageHistoryCategory

data class MessageHistoryKey(
    val chatId: Long,
    val category: MessageHistoryCategory
)
