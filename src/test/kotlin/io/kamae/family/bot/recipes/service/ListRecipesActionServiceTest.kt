package io.kamae.family.bot.recipes.service

import io.kamae.family.bot.core.service.AbstractDefaultActionServiceTest
import io.kamae.family.bot.recipes.client.RecipesServiceClient
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.assertNull
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton

class ListRecipesActionServiceTest : AbstractDefaultActionServiceTest() {
    @MockK
    private lateinit var recipesServiceClient: RecipesServiceClient

    @InjectMockKs
    private lateinit var listRecipesActionService: ListRecipesActionService

    @Test
    fun executeAndGetResponse_success() {
        every { recipesServiceClient.listRecipes() } returns TEST_RECIPES_LIST_RS

        val result = listRecipesActionService.executeAction(formAction())

        verify { recipesServiceClient.listRecipes() }

        val expectedText = "Выберите рецепт"
        assertNull(result.nextQuestion)
        assertEquals(expectedText, result.telegramResponse.text)
        assertEquals(TEST_CHAT_ID, result.telegramResponse.chatId)
        assertNotNull(result.telegramResponse.keyboard)

        val keyboard = (result.telegramResponse.keyboard!!.getKeyboard() as InlineKeyboardMarkup).keyboard
        assertEquals(TEST_RECIPE_TITLE, keyboard[0][0].text)
        assertEquals("/get-recipe $TEST_RECIPE_ID", keyboard[0][0].callbackData)
        assertEquals(TEST_ANOTHER_RECIPE_TITLE, keyboard[1][0].text)
        assertEquals("/get-recipe $TEST_ANOTHER_RECIPE_ID", keyboard[1][0].callbackData)

        verify {
            telegramBotMessageSender.sendMessage(
                baseMessageBuilder(expectedText).replyMarkup(
                    InlineKeyboardMarkup.builder().keyboardRow(
                        listOf(
                            InlineKeyboardButton.builder()
                                .text(TEST_RECIPE_TITLE)
                                .callbackData("/get-recipe $TEST_RECIPE_ID")
                                .build(),
                        )
                    ).keyboardRow(
                        listOf(
                            InlineKeyboardButton.builder()
                                .text(TEST_ANOTHER_RECIPE_TITLE)
                                .callbackData("/get-recipe $TEST_ANOTHER_RECIPE_ID")
                                .build()
                        )
                    ).build()
                ).build()
            )
        }
    }

    @Test
    fun executeAndGetResponse_apiError() {
        every { recipesServiceClient.listRecipes() } throws IllegalStateException("error")

        val result = listRecipesActionService.executeAction(formAction())

        val expectedMessage = "Неизвестная ошибка: error"
        verify { recipesServiceClient.listRecipes() }
        assertEquals(TEST_CHAT_ID, result.telegramResponse.chatId)
        assertEquals(expectedMessage, result.telegramResponse.text)
        assertNull(result.telegramResponse.keyboard)

        verifySenderOnlyMessage(expectedMessage)
    }
}