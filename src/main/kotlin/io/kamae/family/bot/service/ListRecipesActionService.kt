package io.kamae.family.bot.service

import arrow.core.Either
import io.kamae.family.bot.client.RecipesServiceClient
import io.kamae.family.bot.domain.telegram.TelegramActionResult
import io.kamae.family.bot.domain.telegram.dto.TelegramAction
import io.kamae.family.bot.domain.telegram.dto.TelegramButton
import io.kamae.family.bot.domain.telegram.dto.TelegramResponse
import io.kamae.family.bot.service.api.ActionService
import io.kamae.family.bot.service.api.ActionService.Companion.prepareResultWithText
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
@PreAuthorize("hasRole('EDITOR')")
class ListRecipesActionService(private val recipesServiceClient: RecipesServiceClient) : ActionService {
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
                        it.data.map { recipe -> TelegramButton(recipe.title, "/get ${recipe.id}") })
                )
            })
    }
}