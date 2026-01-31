package io.kamae.family.bot.provider.api

import io.kamae.family.bot.domain.telegram.CommandContext

interface ContextProvider {

    fun hasContext(chatId: Long): Boolean

    fun createContext(chatId: Long, commandContext: CommandContext)

    fun getContextForChatId(chatId: Long): CommandContext?

    fun setNextQuestionForChatId(chatId: Long, question: CommandContext.Question)

    fun appendAnswer(chatId: Long, answer: CommandContext.Answer)

    fun removeContextForChatId(chatId: Long)
}