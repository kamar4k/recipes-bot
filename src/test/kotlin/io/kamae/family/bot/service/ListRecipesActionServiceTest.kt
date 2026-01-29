package io.kamae.family.bot.service

import io.kamae.family.bot.AbstractTest
import io.kamae.family.bot.client.RecipesServiceClient
import io.kamae.family.bot.domain.telegram.dto.TelegramButton
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.assertNull

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

        val result = listRecipesActionService.executeAndGetResponse(formAction())

        verify { recipesServiceClient.listRecipes() }

        assertEquals("Выберите рецепт", result.text)
        assertEquals(TEST_CHAT_ID, result.chatId)
        assertNotNull(result.buttons)
        assertEquals(TelegramButton(TEST_RECIPE_TITLE, "/get $TEST_RECIPE_ID"), result.buttons!![0])
        assertEquals(
            TelegramButton(TEST_ANOTHER_RECIPE_TITLE, "/get $TEST_ANOTHER_RECIPE_ID"),
            result.buttons!![1]
        )
    }

    @Test
    fun executeAndGetResponse_apiError() {
        every { recipesServiceClient.listRecipes() } throws IllegalStateException("error")

        val result = listRecipesActionService.executeAndGetResponse(formAction())

        verify { recipesServiceClient.listRecipes() }
        assertEquals(TEST_CHAT_ID, result.chatId)
        assertEquals("Неизвестная ошибка: error", result.text)
        assertNull(result.buttons)
    }
}