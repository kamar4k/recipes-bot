package io.kamae.recipes.infrastructure.telegram.handler

import io.kamae.recipes.AbstractTest
import io.kamae.recipes.application.port.inbound.ListRecipesUseCase
import io.kamae.recipes.infrastructure.telegram.dto.TelegramButton
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import kotlin.test.assertEquals

class ListRecipesHandlerTest : AbstractTest() {
    @MockK
    private lateinit var listRecipesUseCase: ListRecipesUseCase

    @InjectMockKs
    private lateinit var listRecipesHandler: ListRecipesHandler

    @BeforeAll
    fun setUp() {
        MockKAnnotations.init(this)
    }

    @Test
    fun executeCommand_success() {
        every { listRecipesUseCase.getRecipeInfoList() } returns TEST_RECIPE_SHORT_INFO_LIST

        val result = listRecipesHandler.executeCommand(null)

        assertEquals("Выберите рецепт", result.text)
        assertNotNull(result.buttons)
        assertEquals(TelegramButton(TEST_RECIPE_TITLE, "/get $TEST_RECIPE_ID"), result.buttons!![0])
        assertEquals(TelegramButton(TEST_ANOTHER_RECIPE_TITLE, "/get $TEST_ANOTHER_RECIPE_ID"), result.buttons!![1])
    }
}