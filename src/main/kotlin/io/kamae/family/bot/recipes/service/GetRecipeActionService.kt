package io.kamae.family.bot.recipes.service

import arrow.core.Either
import feign.FeignException.NotFound
import io.kamae.family.bot.core.api.ActionService
import io.kamae.family.bot.core.api.ActionService.Companion.prepareResultWithText
import io.kamae.family.bot.core.domain.model.TelegramAction
import io.kamae.family.bot.core.domain.model.TelegramActionResult
import io.kamae.family.bot.recipes.client.RecipesServiceClient
import io.kamae.family.bot.recipes.client.dto.RecipeRsDto
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import java.util.*

@Service
@PreAuthorize("hasRole('RECIPES_READER')")
class GetRecipeActionService(private val recipesServiceClient: RecipesServiceClient) : ActionService {
    override fun executeAndGetResult(telegramAction: TelegramAction): TelegramActionResult {
        val textId = telegramAction.commandContext.text
        val parsedUUID = Either.catch { UUID.fromString(textId) }

        return parsedUUID.fold(
            {
                prepareResultWithText(
                    "Некорректный идентификатор рецепта ($textId). Требуется идентификатор формата UUID",
                    telegramAction
                )

            },
            {
                getRecipeById(it, textId, telegramAction)
            }
        )
    }

    private fun getRecipeById(
        it: UUID,
        textId: String?,
        telegramAction: TelegramAction
    ): TelegramActionResult {
        val result = Either.catch { recipesServiceClient.getRecipe(it) }

        return result.fold({ ex ->
            when {
                ex is NotFound -> prepareResultWithText(
                    "Рецепт с идентификатором $textId не найден",
                    telegramAction
                )

                else -> prepareResultWithText("Неизвестная ошибка: ${ex.message}", telegramAction)
            }
        },
            { rs ->
                prepareResultWithText(generateRecipeMessage(rs), telegramAction)
            }
        )
    }

    private fun generateRecipeMessage(recipeDto: RecipeRsDto): String {
        val sb = StringBuilder(recipeDto.title)

        sb.append("\nИнгридиенты:\n")
        sb.append(recipeDto.ingredients.joinToString(separator = "\n") { "- $it" })
        sb.append("\n\nИнструкции:\n")
        sb.append(recipeDto.instructions)

        return sb.toString()
    }
}