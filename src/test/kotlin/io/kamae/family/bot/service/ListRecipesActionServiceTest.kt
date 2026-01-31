package io.kamae.family.bot.service

import io.kamae.family.bot.AbstractTest
import io.kamae.family.bot.client.RecipesServiceClient
import io.kamae.family.bot.domain.telegram.dto.TelegramButton
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals

class ListRecipesActionServiceTest : AbstractTest() {
    @MockK
    private lateinit var recipesServiceClient: RecipesServiceClient

    @InjectMockKs
    private lateinit var listRecipesActionService: ListRecipesActionService

    @BeforeAll
    fun init() {
        MockKAnnotations.init(this)
    }

    @Test
    fun executeAndGetResponse_success() {
        every { recipesServiceClient.listRecipes() } returns TEST_RECIPES_LIST_RS

        val result = listRecipesActionService.executeAndGetResult(formAction())

        verify { recipesServiceClient.listRecipes() }

        assertNull(result.nextQuestion)
        assertEquals("Выберите рецепт", result.telegramResponse.text)
        assertEquals(TEST_CHAT_ID, result.telegramResponse.chatId)
        assertNotNull(result.telegramResponse.buttons)
        assertEquals(TelegramButton(TEST_RECIPE_TITLE, "/get $TEST_RECIPE_ID"), result.telegramResponse.buttons!![0])
        assertEquals(
            TelegramButton(TEST_ANOTHER_RECIPE_TITLE, "/get $TEST_ANOTHER_RECIPE_ID"),
            result.telegramResponse.buttons!![1]
        )
    }

    @Test
    fun executeAndGetResponse_apiError() {
        every { recipesServiceClient.listRecipes() } throws IllegalStateException("error")

        val result = listRecipesActionService.executeAndGetResult(formAction())

        verify { recipesServiceClient.listRecipes() }
        assertEquals(TEST_CHAT_ID, result.telegramResponse.chatId)
        assertEquals("Неизвестная ошибка: error", result.telegramResponse.text)
        assertNull(result.telegramResponse.buttons)
    }
}