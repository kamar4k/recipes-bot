package io.kamae.family.bot.service

import arrow.core.Either
import feign.RetryableException
import io.kamae.family.bot.client.RecipesServiceClient
import io.kamae.family.bot.client.dto.PostRecipeRqDto
import io.kamae.family.bot.domain.telegram.TelegramActionResult
import io.kamae.family.bot.domain.telegram.dto.TelegramAction
import io.kamae.family.bot.service.api.ActionService
import io.kamae.family.bot.service.api.ActionService.Companion.prepareResultWithText
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
@PreAuthorize("hasRole('EDITOR')")
class AddRecipeActionService(private val recipesServiceClient: RecipesServiceClient) : ActionService {
    override fun executeAndGetResult(telegramAction: TelegramAction): TelegramActionResult {
        checkNotNull(telegramAction.commandContext.text) { "Данная команда требует текста" }
        val recipe = parseRecipe(telegramAction.commandContext.text, telegramAction.telegramUserInfo.username)

        val result = Either.catch { recipesServiceClient.addRecipe(recipe) }

        return result.fold(
            {
                when {
                    it is RetryableException -> prepareResultWithText("Сервис недоступен", telegramAction)
                    else -> prepareResultWithText("Неизвестная ошибка: ${it.message}", telegramAction)
                }
            },
            {
                prepareResultWithText("Рецепт успешно добавлен", telegramAction)
            }
        )
    }

    private fun parseRecipe(text: String, author: String): PostRecipeRqDto {
        val title = text.substringBefore("\n")

        val next = text.substringAfter("\n")

        val ingredientsStr = next.substringBefore("\n\n")
        val ingredientsList = ingredientsStr.split("\n")

        val instructions = next.substringAfter("\n\n")

        return PostRecipeRqDto(null, title, ingredientsList, instructions, author)
    }
}