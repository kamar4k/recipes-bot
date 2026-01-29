package io.kamae.family.bot.service

import feign.Request
import feign.RetryableException
import io.kamae.family.bot.AbstractTest
import io.kamae.family.bot.TestUtils
import io.kamae.family.bot.client.RecipesServiceClient
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class AddRecipeActionServiceTest : AbstractTest() {
    companion object {
        private val TEST_TEXT = TestUtils.getTestResourcesAsString(
            AddRecipeActionServiceTest::class.java, "telegramText"
        ).replace("\r\n", "\n")
    }

    @MockK
    private lateinit var recipesServiceClient: RecipesServiceClient

    @InjectMockKs
    private lateinit var addRecipeActionService: AddRecipeActionService

    @BeforeAll
    fun init() {
        MockKAnnotations.init(this)
    }

    @Test
    fun executeAndGetResponse_success() {

        justRun { recipesServiceClient.addRecipe(any()) }

        val result = addRecipeActionService.executeAndGetResponse(formAction(text = TEST_TEXT))

        verify { recipesServiceClient.addRecipe(TEST_RECIPE_DTO) }

        assertEquals("Рецепт успешно добавлен", result.text)
        assertEquals(TEST_CHAT_ID, result.chatId)
        assertNull(result.buttons)
    }

    @Test
    fun executeAndGetResponse_nullText() {
        val error = assertThrows<IllegalStateException> { addRecipeActionService.executeAndGetResponse(formAction()) }

        assertEquals("Данная команда требует текста", error.message)
    }

    @ParameterizedTest
    @MethodSource("apiErrorCases")
    fun executeAndGetResponse_apiError(exception: Exception, expectedMessage: String) {
        every { recipesServiceClient.addRecipe(any()) } throws exception

        val response = addRecipeActionService.executeAndGetResponse(formAction(text = TEST_TEXT))

        verify { recipesServiceClient.addRecipe(TEST_RECIPE_DTO) }
        assertEquals(expectedMessage, response.text)
        assertEquals(TEST_CHAT_ID, response.chatId)
        assertNull(response.buttons)
    }

    private fun apiErrorCases() = listOf(
        Arguments.of(
            RetryableException(500, "error", Request.HttpMethod.POST, 132L, mockk<Request>()),
            "Сервис недоступен"
        ),
        Arguments.of(IllegalStateException("error"), "Неизвестная ошибка: error")
    )
}