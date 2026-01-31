package io.kamae.family.bot.service

import feign.Request
import feign.RetryableException
import io.kamae.family.bot.AbstractTest
import io.kamae.family.bot.client.RecipesServiceClient
import io.kamae.family.bot.domain.telegram.CommandContext
import io.kamae.family.bot.domain.telegram.dto.TelegramAction
import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource

class AddRecipeActionServiceTest : AbstractTest() {
    companion object {
        private const val TITLE_MSG = "Введите наименование рецепта. /cancel - отменить"
        private const val INGREDIENT_MSG = "Введите ингредиент. /cancel - отменить, /ready - завершить ввод"
        private const val INSTRUCTION_MSG = "Введите инструкции по приготовлению. /cancel - отменить"
        private const val READY_MSG = "Рецепт успешно добавлен"
        private const val CANCEL_MSG = "Ввод рецепта отменён"
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
    fun executeAndGetResponse_cancel() {
        val sequence = listOf(
            createElement("INPUT_NAME", TEST_RECIPE_TITLE),
            createElement("INPUT_INGREDIENT", INGREDIENT_1),
            createElement("INPUT_INGREDIENT", "/cancel")
        )

        val result = addRecipeActionService.executeAndGetResult(
            TelegramAction(
                CommandContext("/add", null, sequence), TEST_USER_INFO
            )
        )

        assertNull(result.nextQuestion)
        assertEquals(CANCEL_MSG, result.telegramResponse.text)
        assertEquals(TEST_CHAT_ID, result.telegramResponse.chatId)
    }

    @ParameterizedTest
    @MethodSource("apiErrorCases")
    fun executeAndGetResponse_apiError(exception: Exception, expectedMessage: String) {
        every { recipesServiceClient.addRecipe(any()) } throws exception

        val result = addRecipeActionService.executeAndGetResult(fullInput())

        verify { recipesServiceClient.addRecipe(TEST_RECIPE_DTO) }
        assertNull(result.nextQuestion)
        assertEquals(expectedMessage, result.telegramResponse.text)
        assertEquals(TEST_CHAT_ID, result.telegramResponse.chatId)
        assertNull(result.telegramResponse.buttons)
    }

    @Test
    fun executeAndGetResponse_complexTest() {

        justRun { recipesServiceClient.addRecipe(any()) }

        val sequence = mutableListOf<CommandContext.Element>()
        checkInitial()
        checkTitleInputs(sequence)
        checkIngredientInputs(sequence, INGREDIENT_1)
        checkIngredientInputs(sequence, INGREDIENT_2)
        checkIngredientInputs(sequence, INGREDIENT_3)
        checkReadyInputs(sequence)
        checkInstructionsInputs(sequence)
    }

    private fun checkInitial() {
        val result =
            addRecipeActionService.executeAndGetResult(TelegramAction(CommandContext("/add", null), TEST_USER_INFO))

        assertEquals(TITLE_MSG, result.telegramResponse.text)
        assertEquals("INPUT_NAME", result.nextQuestion?.value)
        assertEquals(TEST_CHAT_ID, result.telegramResponse.chatId)
        assertNull(result.telegramResponse.buttons)
    }

    private fun checkTitleInputs(sequence: MutableList<CommandContext.Element>) {
        val result =
            addRecipeActionService.executeAndGetResult(getActionForElement(sequence, "INPUT_NAME", TEST_RECIPE_TITLE))
        assertEquals(INGREDIENT_MSG, result.telegramResponse.text)
        assertEquals("INPUT_INGREDIENT", result.nextQuestion?.value)
        assertEquals(TEST_CHAT_ID, result.telegramResponse.chatId)
        assertNull(result.telegramResponse.buttons)
    }

    private fun checkIngredientInputs(sequence: MutableList<CommandContext.Element>, ingredient: String) {
        val result =
            addRecipeActionService.executeAndGetResult(getActionForElement(sequence, "INPUT_INGREDIENT", ingredient))
        assertEquals(INGREDIENT_MSG, result.telegramResponse.text)
        assertEquals("INPUT_INGREDIENT", result.nextQuestion?.value)
        assertEquals(TEST_CHAT_ID, result.telegramResponse.chatId)
        assertNull(result.telegramResponse.buttons)
    }

    private fun checkReadyInputs(sequence: MutableList<CommandContext.Element>) {
        val result =
            addRecipeActionService.executeAndGetResult(getActionForElement(sequence, "INPUT_INGREDIENT", "/ready"))
        assertEquals(INSTRUCTION_MSG, result.telegramResponse.text)
        assertEquals("INPUT_INSTRUCTIONS", result.nextQuestion?.value)
        assertEquals(TEST_CHAT_ID, result.telegramResponse.chatId)
        assertNull(result.telegramResponse.buttons)
    }

    private fun checkInstructionsInputs(sequence: MutableList<CommandContext.Element>) {
        val result = addRecipeActionService.executeAndGetResult(
            getActionForElement(
                sequence,
                "INPUT_INSTRUCTIONS",
                TEST_RECIPE_INSTRUCTIONS
            )
        )
        assertNull(result.nextQuestion)
        assertEquals(READY_MSG, result.telegramResponse.text)
        assertEquals(TEST_CHAT_ID, result.telegramResponse.chatId)
        assertNull(result.telegramResponse.buttons)

        verify { recipesServiceClient.addRecipe(TEST_RECIPE_DTO) }
    }

    private fun apiErrorCases() = listOf(
        Arguments.of(
            RetryableException(500, "error", Request.HttpMethod.POST, 132L, mockk<Request>()),
            "Сервис недоступен"
        ),
        Arguments.of(IllegalStateException("error"), "Неизвестная ошибка: error")
    )

    private fun fullInput() = TelegramAction(
        CommandContext(
            "/add",
            null,
            listOf(
                createElement("INPUT_NAME", TEST_RECIPE_TITLE),
                createElement("INPUT_INGREDIENT", INGREDIENT_1),
                createElement("INPUT_INGREDIENT", INGREDIENT_2),
                createElement("INPUT_INGREDIENT", INGREDIENT_3),
                createElement("INPUT_INGREDIENT", "/ready"),
                createElement("INPUT_INSTRUCTIONS", TEST_RECIPE_INSTRUCTIONS)
            )
        ),
        TEST_USER_INFO
    )

    private fun getActionForElement(sequence: MutableList<CommandContext.Element>, question: String, answer: String) =
        TelegramAction(
            CommandContext("/add", null, withElement(sequence, question, answer)),
            TEST_USER_INFO
        )

    private fun withElement(
        sequence: MutableList<CommandContext.Element>,
        question: String,
        answer: String
    ): List<CommandContext.Element> {
        sequence.add(createElement(question, answer))

        return buildList { addAll(sequence) }
    }

    private fun createElement(question: String, answer: String) =
        CommandContext.Element(CommandContext.Question(question), CommandContext.Answer(answer))
}