package io.kamae.family.bot.core.api

import io.kamae.family.bot.core.api.model.MessageHistoryCategory
import io.kamae.family.bot.core.api.model.MessageHistoryElement

interface MessageHistoryProvider {
    fun getHistory(chatId: Long, category: MessageHistoryCategory): List<MessageHistoryElement>

    fun addToHistory(chatId: Long, category: MessageHistoryCategory, element: MessageHistoryElement)

    fun addToHistory(chatId: Long, category: MessageHistoryCategory, vararg elements: MessageHistoryElement)

    fun removeHistory(chatId: Long, category: MessageHistoryCategory)
}