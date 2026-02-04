package io.kamae.family.bot.recipes.service

import feign.FeignException.NotFound
import feign.Request
import io.kamae.family.bot.AbstractTest
import io.kamae.family.bot.recipes.client.RecipesServiceClient
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class GetRecipeActionServiceTest : AbstractTest() {
    @MockK
    private lateinit var recipesServiceClient: RecipesServiceClient

    @InjectMockKs
    private lateinit var getRecipeActionService: GetRecipeActionService

    @BeforeAll
    fun init() {
        MockKAnnotations.init(this)
    }

    @Test
    fun executeAndGetResponse_success() {
        every { recipesServiceClient.getRecipe(TEST_RECIPE_ID) } returns TEST_RECIPE_DTO_WITH_ID

        val result = getRecipeActionService.executeAndGetResult(formAction(text = TEST_RECIPE_ID.toString()))


        verify { recipesServiceClient.getRecipe(TEST_RECIPE_ID) }

        val expectedResponse = getTestResourcesAsString("telegramResponseText").replace("\r\n", "\n")

        assertNull(result.nextQuestion)
        assertEquals(TEST_CHAT_ID, result.telegramResponse.chatId)
        assertNull(result.telegramResponse.buttons)
        assertEquals(expectedResponse, result.telegramResponse.text)
    }

    @Test
    fun executeAndGetResponse_incorrectUUID() {
        val result = getRecipeActionService.executeAndGetResult(formAction(text = "1234"))

        assertNull(result.nextQuestion)
        assertEquals(TEST_CHAT_ID, result.telegramResponse.chatId)
        assertNull(result.telegramResponse.buttons)
        assertEquals(
            "Некорректный идентификатор рецепта (1234). Требуется идентификатор формата UUID",
            result.telegramResponse.text
        )
    }

    @ParameterizedTest
    @MethodSource("apiErrorCases")
    fun executeAndGetResponse_apiError(exception: Exception, expectedMessage: String) {
        every { recipesServiceClient.getRecipe(TEST_RECIPE_ID) } throws exception

        val result = getRecipeActionService.executeAndGetResult(formAction(text = TEST_RECIPE_ID.toString()))

        verify { recipesServiceClient.getRecipe(TEST_RECIPE_ID) }
        assertEquals(TEST_CHAT_ID, result.telegramResponse.chatId)
        assertNull(result.telegramResponse.buttons)
        assertEquals(expectedMessage, result.telegramResponse.text)
    }

    private fun apiErrorCases() = listOf(
        Arguments.of(IllegalStateException("error"), "Неизвестная ошибка: error"),
        Arguments.of(
            NotFound("nf", mockk<Request>(), byteArrayOf(), mapOf()),
            "Рецепт с идентификатором $TEST_RECIPE_ID не найден"
        )
    )

}