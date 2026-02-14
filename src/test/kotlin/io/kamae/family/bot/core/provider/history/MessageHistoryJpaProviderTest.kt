package io.kamae.family.bot.core.provider.history

import io.kamae.family.bot.AbstractIntegrationTest
import io.kamae.family.bot.core.jpa.repository.MessageHistoryRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class MessageHistoryJpaProviderTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var messageHistoryRepository: MessageHistoryRepository

    @Autowired
    private lateinit var messageHistoryJpaProvider: MessageHistoryJpaProvider

    @AfterEach
    fun clearRepository() {
        messageHistoryRepository.deleteAll()
    }

    @Test
    fun getHistory() {
        messageHistoryRepository.saveAll(listOf(TEST_MSG_HISTORY_ENTITY, TEST_MSG_HISTORY_ENTITY_ANOTHER))

        val results = messageHistoryJpaProvider.getHistory(TEST_CHAT_ID, TEST_MSG_HISTORY_CATEGORY)

        assertEquals(2, results.size)
        assertEquals(TEST_MSG_HISTORY, results[0])
        assertEquals(TEST_MSG_HISTORY_ANOTHER, results[1])
    }

    @Test
    fun addToHistory() {
        messageHistoryRepository.save(TEST_MSG_HISTORY_ENTITY)

        messageHistoryJpaProvider.addToHistory(TEST_CHAT_ID, TEST_MSG_HISTORY_CATEGORY, TEST_MSG_HISTORY_ANOTHER)

        val results = messageHistoryRepository.findAll()

        assertEquals(2, results.size)

        assertEquals(TEST_MSG_ID, results[0].messageId)
        assertEquals(TEST_CHAT_ID, results[0].chatId)
        assertEquals(TEST_MSG_HISTORY_CATEGORY.value, results[0].category)
        assertEquals(TEST_MSG_HISTORY_DATE_TIME, results[0].createdAt)

        assertEquals(TEST_MSG_ID_ANOTHER, results[1].messageId)
        assertEquals(TEST_CHAT_ID, results[1].chatId)
        assertEquals(TEST_MSG_HISTORY_CATEGORY.value, results[1].category)
        assertEquals(TEST_MSG_HISTORY_DATE_TIME_ANOTHER, results[1].createdAt)
    }

    @Test
    fun addToHistory_twoElements() {
        messageHistoryJpaProvider.addToHistory(
            TEST_CHAT_ID,
            TEST_MSG_HISTORY_CATEGORY,
            TEST_MSG_HISTORY,
            TEST_MSG_HISTORY_ANOTHER
        )

        val results = messageHistoryRepository.findAll()

        assertEquals(2, results.size)

        assertEquals(TEST_MSG_ID, results[0].messageId)
        assertEquals(TEST_CHAT_ID, results[0].chatId)
        assertEquals(TEST_MSG_HISTORY_CATEGORY.value, results[0].category)
        assertEquals(TEST_MSG_HISTORY_DATE_TIME, results[0].createdAt)

        assertEquals(TEST_MSG_ID_ANOTHER, results[1].messageId)
        assertEquals(TEST_CHAT_ID, results[1].chatId)
        assertEquals(TEST_MSG_HISTORY_CATEGORY.value, results[1].category)
        assertEquals(TEST_MSG_HISTORY_DATE_TIME_ANOTHER, results[1].createdAt)
    }

    @Test
    fun addToHistory_zeroElements() {
        val error = assertThrows<IllegalStateException> {
            messageHistoryJpaProvider.addToHistory(
                TEST_CHAT_ID,
                TEST_MSG_HISTORY_CATEGORY
            )
        }

        assertEquals("Должен быть хотя бы один элемент", error.message)

        val results = messageHistoryRepository.findAll()
        assertEquals(0, results.size)
    }

    @Test
    fun removeHistory() {
        messageHistoryRepository.save(TEST_MSG_HISTORY_ENTITY)

        messageHistoryJpaProvider.removeHistory(
            TEST_CHAT_ID,
            TEST_MSG_HISTORY_CATEGORY
        )

        val results = messageHistoryRepository.findAll()
        assertEquals(0, results.size)
    }
}