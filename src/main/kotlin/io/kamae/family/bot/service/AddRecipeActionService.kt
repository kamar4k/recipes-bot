package io.kamae.family.bot.service

import arrow.core.Either
import feign.RetryableException
import io.kamae.family.bot.client.RecipesServiceClient
import io.kamae.family.bot.client.dto.PostRecipeRqDto
import io.kamae.family.bot.domain.telegram.dto.TelegramAction
import io.kamae.family.bot.domain.telegram.dto.TelegramResponse
import io.kamae.family.bot.service.api.ActionService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
@PreAuthorize("hasRole('EDITOR')")
class AddRecipeActionService(private val recipesServiceClient: RecipesServiceClient) : ActionService {
    override fun executeAndGetResponse(telegramAction: TelegramAction): TelegramResponse {
        checkNotNull(telegramAction.telegramParsedRequest.text) { "Данная команда требует текста" }
        val recipe = parseRecipe(telegramAction.telegramParsedRequest.text, telegramAction.telegramUserInfo.username)

        val result = Either.catch { recipesServiceClient.addRecipe(recipe) }

        return result.fold(
            {
                when {
                    it is RetryableException -> prepareResponseWithText("Сервис недоступен", telegramAction)
                    else -> prepareResponseWithText("Неизвестная ошибка: ${it.message}", telegramAction)
                }
            },
            {
                prepareResponseWithText("Рецепт успешно добавлен", telegramAction)
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