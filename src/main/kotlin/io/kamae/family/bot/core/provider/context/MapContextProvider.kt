package io.kamae.family.bot.core.provider.context

import io.kamae.family.bot.core.api.ContextProvider
import io.kamae.family.bot.core.domain.model.CommandContext
import org.springframework.stereotype.Component
import java.util.concurrent.ConcurrentHashMap

@Component
class MapContextProvider: ContextProvider {
    private val contextMap: ConcurrentHashMap<Long, MapCommandContext> = ConcurrentHashMap()

    override fun hasContext(chatId: Long): Boolean {
        return contextMap.containsKey(chatId)
    }

    override fun createContext(chatId: Long, commandContext: CommandContext) {
        val context = MapCommandContext.fromContext(commandContext)
        contextMap[chatId] = context
    }

    override fun getContextForChatId(chatId: Long): CommandContext? {
        return contextMap[chatId]?.toCommandContext()
    }

    override fun setNextQuestionForChatId(chatId: Long, question: CommandContext.Question) {
        contextMap[chatId]!!.nextQuestion = question.value
    }

    override fun appendAnswer(chatId: Long, answer: CommandContext.Answer) {
        val context = contextMap[chatId]!!

        context.addToSequence(MapCommandContext.Element(context.nextQuestion!!, answer.value))
        context.nextQuestion = null
    }

    override fun removeContextForChatId(chatId: Long) {
        contextMap.remove(chatId)
    }
}