package io.kamae.family.bot.core.api.model

import java.time.LocalDateTime

data class MessageHistoryElement(
    val messageId: Int,
    val dateTime: LocalDateTime = LocalDateTime.now()
)
