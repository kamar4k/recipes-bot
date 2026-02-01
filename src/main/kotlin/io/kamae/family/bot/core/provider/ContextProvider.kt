package io.kamae.family.bot.core.provider

import io.kamae.family.bot.core.domain.model.CommandContext

interface ContextProvider {

    fun hasContext(chatId: Long): Boolean

    fun createContext(chatId: Long, commandContext: CommandContext)

    fun getContextForChatId(chatId: Long): CommandContext?

    fun setNextQuestionForChatId(chatId: Long, question: CommandContext.Question)

    fun appendAnswer(chatId: Long, answer: CommandContext.Answer)

    fun removeContextForChatId(chatId: Long)
}