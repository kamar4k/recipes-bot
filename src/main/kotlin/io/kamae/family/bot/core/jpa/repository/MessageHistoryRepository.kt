package io.kamae.family.bot.core.jpa.repository

import io.kamae.family.bot.core.jpa.entity.MessageHistoryEntity
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface MessageHistoryRepository: JpaRepository<MessageHistoryEntity, UUID> {
    fun findAllByChatIdAndCategoryOrderByCreatedAtAsc(chatId: Long, category: String): List<MessageHistoryEntity>

    @Transactional
    fun removeAllByChatIdAndCategory(chatId: Long, category: String)
}