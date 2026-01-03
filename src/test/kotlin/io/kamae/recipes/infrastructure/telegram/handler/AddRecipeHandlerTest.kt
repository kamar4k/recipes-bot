package io.kamae.recipes.infrastructure.telegram.handler

import io.kamae.recipes.AbstractTest
import io.kamae.recipes.application.port.inbound.AddRecipeUseCase
import io.kamae.recipes.infrastructure.telegram.parser.TelegramMessageHandler
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class AddRecipeHandlerTest: AbstractTest() {

    @MockK
    private lateinit var telegramMessageHandler: TelegramMessageHandler

    @MockK
    private lateinit var addRecipeUseCase: AddRecipeUseCase

    @InjectMockKs
    private lateinit var addRecipeHandler: AddRecipeHandler

    @BeforeAll
    fun setUp() {
        MockKAnnotations.init(this)
        every { telegramMessageHandler.parseRecipe(TELEGRAM_COMMAND_TEXT) } returns TEST_RECIPE_DTO
        every { addRecipeUseCase.addRecipe(TEST_RECIPE_DTO) } returns TEST_RECIPE_DTO_WITH_ID
    }

    @Test
    fun executeCommand_success() {
        val result = addRecipeHandler.executeCommand(TELEGRAM_COMMAND_TEXT)

        assertEquals("Рецепт $TEST_RECIPE_TITLE добавлен с идентификатором $TEST_RECIPE_ID", result.text)
    }
}