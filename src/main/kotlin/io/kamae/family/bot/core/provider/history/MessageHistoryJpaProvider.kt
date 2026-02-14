package io.kamae.family.bot.core.provider.history

import io.kamae.family.bot.core.api.MessageHistoryProvider
import io.kamae.family.bot.core.api.model.MessageHistoryCategory
import io.kamae.family.bot.core.api.model.MessageHistoryElement
import io.kamae.family.bot.core.jpa.entity.MessageHistoryEntity
import io.kamae.family.bot.core.jpa.repository.MessageHistoryRepository
import org.springframework.stereotype.Component

@Component
class MessageHistoryJpaProvider(
    private val messageHistoryRepository: MessageHistoryRepository
) : MessageHistoryProvider {
    override fun getHistory(chatId: Long, category: MessageHistoryCategory): List<MessageHistoryElement> {
        val results = messageHistoryRepository.findAllByChatIdAndCategoryOrderByCreatedAtAsc(chatId, category.value)

        return results.map { MessageHistoryElement(it.messageId, it.createdAt) }
    }

    override fun addToHistory(chatId: Long, category: MessageHistoryCategory, element: MessageHistoryElement) {
        val hstEntity = MessageHistoryEntity(chatId, category.value, element.messageId, element.dateTime)

        messageHistoryRepository.save(hstEntity)
    }

    override fun addToHistory(chatId: Long, category: MessageHistoryCategory, vararg elements: MessageHistoryElement) {
        check(elements.isNotEmpty()) { "Должен быть хотя бы один элемент" }
        val hstEntities = elements.map {
            MessageHistoryEntity(chatId, category.value, it.messageId, it.dateTime)
        }

        messageHistoryRepository.saveAll(hstEntities)
    }

    override fun removeHistory(chatId: Long, category: MessageHistoryCategory) {
        messageHistoryRepository.removeAllByChatIdAndCategory(chatId, category.value)
    }
}