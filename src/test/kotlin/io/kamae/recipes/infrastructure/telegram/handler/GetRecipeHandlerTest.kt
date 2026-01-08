package io.kamae.recipes.infrastructure.telegram.handler

import io.kamae.recipes.AbstractTest
import io.kamae.recipes.application.port.inbound.GetRecipeUseCase
import io.kamae.recipes.domain.exception.RecipeNotFoundException
import io.kamae.recipes.infrastructure.telegram.parser.TelegramMessageHandler
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class GetRecipeHandlerTest : AbstractTest() {

    @MockK
    private lateinit var telegramMessageHandler: TelegramMessageHandler

    @MockK
    private lateinit var getRecipeUseCase: GetRecipeUseCase

    @InjectMockKs
    private lateinit var getRecipeHandler: GetRecipeHandler

    @BeforeAll
    fun setUp() {
        MockKAnnotations.init(this)
        every { telegramMessageHandler.generateRecipeMessage(TEST_RECIPE_DTO_WITH_ID) } returns TELEGRAM_RESPONSE_TEXT
    }

    @Test
    fun executeCommand_success() {
        every { getRecipeUseCase.getRecipeById(TEST_RECIPE_ID) } returns TEST_RECIPE_DTO_WITH_ID

        val result = getRecipeHandler.executeCommand(TEST_RECIPE_ID.toString(), TEST_CHAT_ID)

        assertEquals(TELEGRAM_RESPONSE_TEXT, result.text)
    }

    @Test
    fun executeCommand_notFound() {
        every { getRecipeUseCase.getRecipeById(TEST_RECIPE_ID) } throws RecipeNotFoundException(TEST_RECIPE_ID.toString())

        val result = getRecipeHandler.executeCommand(TEST_RECIPE_ID.toString(), TEST_CHAT_ID)

        assertEquals("Рецепт не найден", result.text)
    }
}