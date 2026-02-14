package io.kamae.family.bot.recipes.service

import arrow.core.Either
import io.kamae.family.bot.common.domain.keyboard.OneColumnKeyboard
import io.kamae.family.bot.core.api.ActionService.Companion.prepareResultWithText
import io.kamae.family.bot.core.api.TelegramBotMessageSender
import io.kamae.family.bot.core.domain.model.TelegramAction
import io.kamae.family.bot.core.domain.model.TelegramActionResult
import io.kamae.family.bot.core.domain.model.TelegramButton
import io.kamae.family.bot.core.domain.model.TelegramResponse
import io.kamae.family.bot.core.service.AbstractDefaultActionService
import io.kamae.family.bot.recipes.client.RecipesServiceClient
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
@PreAuthorize("hasRole('RECIPES_EDITOR')")
class ListRecipesActionService(
    private val recipesServiceClient: RecipesServiceClient,
    sender: TelegramBotMessageSender
) : AbstractDefaultActionService(sender) {
    override fun executeAndGetResult(telegramAction: TelegramAction): TelegramActionResult {
        val recipes = Either.catch { recipesServiceClient.listRecipes() }

        return recipes.fold(
            {
                prepareResultWithText("Неизвестная ошибка: ${it.message}", telegramAction)
            },
            {
                TelegramActionResult(
                    TelegramResponse(
                        "Выберите рецепт",
                        telegramAction.telegramUserInfo.chatId,
                        OneColumnKeyboard(it.data.map { recipe -> TelegramButton(recipe.title, "/get-recipe ${recipe.id}") })
                    )
                )
            })
    }
}