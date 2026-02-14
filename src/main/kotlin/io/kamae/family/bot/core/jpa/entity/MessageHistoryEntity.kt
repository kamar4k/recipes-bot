package io.kamae.family.bot.core.jpa.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import java.time.LocalDateTime
import java.util.*

@Entity(name = "message_history")
class MessageHistoryEntity(
    @Column(name = "chat_id")
    val chatId: Long,
    @Column(name = "category")
    val category: String,
    @Column(name = "message_id")
    val messageId: Int,
    @Column(name = "created_at")
    val createdAt: LocalDateTime
) {
    @Id
    @Column(name = "id")
    val id: UUID = UUID.randomUUID()
}