package io.kamae.family.bot.core.provider.context

import io.kamae.family.bot.AbstractTest
import io.kamae.family.bot.core.domain.model.CommandContext
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.test.util.ReflectionTestUtils
import java.util.concurrent.ConcurrentHashMap

class MapContextProviderTest : AbstractTest() {
    private lateinit var mapContextProvider: MapContextProvider

    @BeforeEach
    fun initProvider() {
        mapContextProvider = MapContextProvider()
    }

    @Test
    fun createContext_success() {
        mapContextProvider.createContext(TEST_CHAT_ID, TEST_COMMAND_CONTEXT)
        val storedContext = getContextMap()[TEST_CHAT_ID]

        assertNotNull(storedContext)
        assertEquals(TELEGRAM_COMMAND, storedContext?.command)
        assertEquals(TELEGRAM_COMMAND_TEXT, storedContext?.text)
        assertNull(storedContext?.nextQuestion)
    }

    @Test
    fun getContextForChatId_success() {
        val contextMap = getContextMap()
        contextMap[TEST_CHAT_ID] = TEST_MAP_COMMAND_CONTEXT.copy()

        val result = mapContextProvider.getContextForChatId(TEST_CHAT_ID)

        assertNotNull(result)
        assertEquals(TEST_MAP_COMMAND_CONTEXT.command, result?.command)
        assertEquals(TEST_MAP_COMMAND_CONTEXT.text, result?.text)
    }

    @Test
    fun getContextForChatId_notFound() {
        val result = mapContextProvider.getContextForChatId(TEST_CHAT_ID)

        assertNull(result)
    }

    @Test
    fun setNextQuestionForChatId() {
        val contextMap = getContextMap()
        contextMap[TEST_CHAT_ID] = TEST_MAP_COMMAND_CONTEXT.copy()

        mapContextProvider.setNextQuestionForChatId(TEST_CHAT_ID, CommandContext.Question(TELEGRAM_COMMAND_QUESTION))

        assertEquals(TELEGRAM_COMMAND_QUESTION, contextMap[TEST_CHAT_ID]?.nextQuestion)
    }

    @Suppress("UNCHECKED_CAST")
    @Test
    fun appendAnswer() {
        val contextMap = getContextMap()
        contextMap[TEST_CHAT_ID] = TEST_MAP_COMMAND_CONTEXT.copy()

        mapContextProvider.appendAnswer(TEST_CHAT_ID, CommandContext.Answer(TELEGRAM_COMMAND_ANSWER))

        val result = contextMap[TEST_CHAT_ID]
        val sequence = ReflectionTestUtils.getField(result!!, "sequence")!! as List<MapCommandContext.Element>

        assertEquals(1, sequence.size)
        assertEquals(TELEGRAM_COMMAND_QUESTION, sequence[0].question)
        assertEquals(TELEGRAM_COMMAND_ANSWER, sequence[0].answer)
        assertNull(result.nextQuestion)
    }

    @Test
    fun removeContextForChatId() {
        val contextMap = getContextMap()
        contextMap[TEST_CHAT_ID] = TEST_MAP_COMMAND_CONTEXT.copy()

        mapContextProvider.removeContextForChatId(TEST_CHAT_ID)

        assertNull(contextMap[TEST_CHAT_ID])
    }

    @Suppress("UNCHECKED_CAST")
    private fun getContextMap(): ConcurrentHashMap<Long, MapCommandContext> {
        return ReflectionTestUtils.getField(
            mapContextProvider,
            "contextMap"
        )!! as ConcurrentHashMap<Long, MapCommandContext>
    }
}