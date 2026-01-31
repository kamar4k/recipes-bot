package io.kamae.family.bot

import io.kamae.family.bot.client.dto.ListRecipesRsDto
import io.kamae.family.bot.client.dto.PostRecipeRqDto
import io.kamae.family.bot.client.dto.RecipeRsDto
import io.kamae.family.bot.client.dto.RecipeShortInfoDto
import io.kamae.family.bot.domain.telegram.CommandContext
import io.kamae.family.bot.domain.telegram.dto.TelegramAction
import io.kamae.family.bot.provider.context.MapCommandContext
import io.mockk.clearAllMocks
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.TestInstance
import org.springframework.test.context.ActiveProfiles
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
abstract class AbstractTest {
    companion object {
        const val TEST_CHAT_ID: Long = 1234L
        val TEST_RECIPE_ID: UUID = UUID.fromString("e07446c5-b71b-4c48-a483-1f8eefd80f6e")
        const val TEST_RECIPE_TITLE = "Recipe Title"
        private const val TEST_RECIPE_INSTRUCTIONS = "Step1\nStep2\nStep3\nStep4"
        private val TEST_RECIPE_INGREDIENTS = listOf("ingridient1", "ingridient2 3", "ing3 200g")
        val TEST_ANOTHER_RECIPE_ID: UUID = UUID.fromString("2be1cc47-3b78-422c-b388-44b8be04eab1")
        const val TEST_ANOTHER_RECIPE_TITLE = "Another Recipe Title"

        const val TEST_AUTHOR = "user"

        val TEST_RECIPE_DTO = PostRecipeRqDto(
            null,
            TEST_RECIPE_TITLE,
            TEST_RECIPE_INGREDIENTS,
            TEST_RECIPE_INSTRUCTIONS,
            TEST_AUTHOR
        )

        val TEST_RECIPE_DTO_WITH_ID = RecipeRsDto(
            TEST_RECIPE_ID,
            TEST_RECIPE_TITLE,
            TEST_RECIPE_INGREDIENTS,
            TEST_RECIPE_INSTRUCTIONS,
            TEST_AUTHOR
        )

        const val TELEGRAM_COMMAND = "/command"
        const val TELEGRAM_COMMAND_QUESTION = "question"
        const val TELEGRAM_COMMAND_ANSWER = "answer"
        const val TELEGRAM_COMMAND_TEXT = "some command text"
        const val TELEGRAM_MESSAGE_TEXT = "some text"
        const val TELEGRAM_RESPONSE_TEXT = "some response text"

        private val TEST_RECIPE_SHORT_INFO_LIST = listOf(
            RecipeShortInfoDto(TEST_RECIPE_ID, TEST_RECIPE_TITLE),
            RecipeShortInfoDto(TEST_ANOTHER_RECIPE_ID, TEST_ANOTHER_RECIPE_TITLE)
        )

        val TEST_RECIPES_LIST_RS = ListRecipesRsDto(TEST_RECIPE_SHORT_INFO_LIST)

        val TEST_COMMAND_CONTEXT = CommandContext(
            TELEGRAM_COMMAND, TELEGRAM_COMMAND_TEXT
        )

        val TEST_MAP_COMMAND_CONTEXT = MapCommandContext(
            TELEGRAM_COMMAND, TELEGRAM_COMMAND_TEXT, TELEGRAM_COMMAND_QUESTION
        )
    }

    @AfterEach
    fun clearMocks() {
        clearAllMocks()
    }

    protected fun formAction(command: String = "/any", text: String? = null) =
        TelegramAction(CommandContext(command, text), TEST_CHAT_ID, TEST_AUTHOR)

    protected fun getTestResourcesAsString(fileName: String): String {
        return TestUtils.getTestResourcesAsString(this.javaClass, fileName)
    }
}