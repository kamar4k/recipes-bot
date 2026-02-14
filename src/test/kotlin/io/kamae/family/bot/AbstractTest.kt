package io.kamae.family.bot

import io.kamae.family.bot.core.api.model.MessageHistoryCategory
import io.kamae.family.bot.core.api.model.MessageHistoryElement
import io.kamae.family.bot.core.domain.model.CommandContext
import io.kamae.family.bot.core.domain.model.TelegramAction
import io.kamae.family.bot.core.domain.model.TelegramUserInfo
import io.kamae.family.bot.core.jpa.entity.MessageHistoryEntity
import io.kamae.family.bot.core.provider.context.MapCommandContext
import io.kamae.family.bot.purchases.client.dto.*
import io.kamae.family.bot.purchases.constants.PurchasesConstants.PRODUCT_LIST_CATEGORY
import io.kamae.family.bot.recipes.client.dto.ListRecipesRsDto
import io.kamae.family.bot.recipes.client.dto.PostRecipeRqDto
import io.kamae.family.bot.recipes.client.dto.RecipeRsDto
import io.kamae.family.bot.recipes.client.dto.RecipeShortInfoDto
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import org.springframework.test.context.ActiveProfiles
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import java.time.LocalDateTime
import java.util.*

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ActiveProfiles("test")
abstract class AbstractTest {
    companion object {
        const val TEST_CHAT_ID: Long = 1234L
        const val TEST_MSG_ID = 123
        const val TEST_MSG_ID_ANOTHER = 1234
        val TEST_MSG_HISTORY_CATEGORY = MessageHistoryCategory("Category")
        val PROD_LIST_HISTORY_CATEGORY = MessageHistoryCategory(PRODUCT_LIST_CATEGORY)
        val TEST_MSG_HISTORY_DATE_TIME = LocalDateTime.parse("2025-12-30T12:12:11.123")
        val TEST_MSG_HISTORY_DATE_TIME_ANOTHER = LocalDateTime.parse("2025-12-30T12:13:11.123")
        val TEST_MSG_HISTORY_ENTITY = MessageHistoryEntity(
            TEST_CHAT_ID,
            TEST_MSG_HISTORY_CATEGORY.value,
            TEST_MSG_ID,
            TEST_MSG_HISTORY_DATE_TIME
        )
        val TEST_MSG_HISTORY_ENTITY_ANOTHER = MessageHistoryEntity(
            TEST_CHAT_ID,
            TEST_MSG_HISTORY_CATEGORY.value,
            TEST_MSG_ID_ANOTHER,
            TEST_MSG_HISTORY_DATE_TIME_ANOTHER
        )

        val TEST_MSG_HISTORY = MessageHistoryElement(TEST_MSG_ID, TEST_MSG_HISTORY_DATE_TIME)
        val TEST_MSG_HISTORY_ANOTHER = MessageHistoryElement(TEST_MSG_ID_ANOTHER, TEST_MSG_HISTORY_DATE_TIME_ANOTHER)

        val TEST_RECIPE_ID: UUID = UUID.fromString("e07446c5-b71b-4c48-a483-1f8eefd80f6e")
        const val TEST_RECIPE_TITLE = "Recipe Title"
        const val TEST_RECIPE_INSTRUCTIONS = "Step1\nStep2\nStep3\nStep4"
        const val INGREDIENT_1 = "ingridient1"
        const val INGREDIENT_2 = "ingridient2 3"
        const val INGREDIENT_3 = "ing3 200g"
        val TEST_RECIPE_INGREDIENTS_STR = "$INGREDIENT_1\n$INGREDIENT_2\n$INGREDIENT_3"
        private val TEST_RECIPE_INGREDIENTS = listOf(INGREDIENT_1, INGREDIENT_2, INGREDIENT_3)
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

        val TEST_USER_INFO = TelegramUserInfo(TEST_CHAT_ID, TEST_AUTHOR)

        val TEST_PRODUCT_ID: UUID = UUID.fromString("f47ac10b-58cc-4372-a567-0e02b2c3d479")
        const val TEST_PRODUCT_NAME = "Product Name"
        const val TEST_PRODUCT_CURR_QUANTITY = 123.0
        val TEST_PRODUCT_PREDICATION: LocalDateTime = LocalDateTime.parse("2026-01-03T21:59:46.123")
        const val TEST_PRODUCT_PREDICATION_STR = "03.01.2026"

        val TEST_PRODUCT_ID_ANOTHER: UUID = UUID.fromString("047ac10b-58cc-4372-a567-0e02b2c3d479")
        const val TEST_PRODUCT_NAME_ANOTHER = "Product Name 2"
        const val TEST_PRODUCT_CURR_QUANTITY_ANOTHER = 0.0
        val TEST_PRODUCT_PREDICATION_ANOTHER: LocalDateTime = LocalDateTime.parse("2026-01-04T21:59:47.123")
        const val TEST_PRODUCT_PREDICATION_ANOTHER_STR = "04.01.2026"

        val CREATE_PRODUCT_DTO = CreateProductRqDto(
            TEST_PRODUCT_NAME, ProductEventDto(ChangeType.INCREASE, TEST_PRODUCT_CURR_QUANTITY)
        )

        val ADD_EVENT_DTO = AddProductEventRqDto(
            ProductEventDto(ChangeType.REDUCE, TEST_PRODUCT_CURR_QUANTITY)
        )

        val GET_PRODUCTS_INFO_RS_DTO = GetProductsInfoRsDto(
            listOf(
                ProductInfoDto(
                    TEST_PRODUCT_ID,
                    TEST_PRODUCT_NAME,
                    TEST_PRODUCT_CURR_QUANTITY,
                    TEST_PRODUCT_PREDICATION
                ),
                ProductInfoDto(
                    TEST_PRODUCT_ID_ANOTHER,
                    TEST_PRODUCT_NAME_ANOTHER,
                    TEST_PRODUCT_CURR_QUANTITY_ANOTHER,
                    TEST_PRODUCT_PREDICATION_ANOTHER
                ),
            )
        )
    }

    @AfterEach
    fun clearMocks() {
        clearAllMocks()
    }

    @BeforeAll
    fun init() {
        MockKAnnotations.init(this)
    }

    protected fun baseMessageBuilder(message: String): SendMessage.SendMessageBuilder =
        SendMessage.builder().text(message).chatId(TEST_CHAT_ID)


    protected fun formAction(command: String = "/any", text: String? = null) =
        TelegramAction(CommandContext(command, text), TEST_USER_INFO)

    protected fun formActionWithContext(
        command: String = "/any",
        text: String? = null,
        context: List<CommandContext.Element>
    ) =
        TelegramAction(CommandContext(command, text, context), TEST_USER_INFO, TEST_MSG_ID)


    protected fun getTestResourcesAsString(fileName: String): String {
        return TestUtils.getTestResourcesAsString(this.javaClass, fileName)
    }
}