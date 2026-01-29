package io.kamae.family.bot.service

import arrow.core.Either
import feign.FeignException.NotFound
import io.kamae.family.bot.client.RecipesServiceClient
import io.kamae.family.bot.client.dto.RecipeRsDto
import io.kamae.family.bot.domain.telegram.dto.TelegramAction
import io.kamae.family.bot.domain.telegram.dto.TelegramResponse
import io.kamae.family.bot.service.api.ActionService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service
import java.util.*

@Service
@PreAuthorize("hasRole('READER')")
class GetRecipeActionService(private val recipesServiceClient: RecipesServiceClient) : ActionService {
    override fun executeAndGetResponse(telegramAction: TelegramAction): TelegramResponse {
        val textId = telegramAction.telegramParsedRequest.text
        val parsedUUID = Either.catch { UUID.fromString(textId) }

        return parsedUUID.fold(
            {
                prepareResponseWithText(
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
    ): TelegramResponse {
        val result = Either.catch { recipesServiceClient.getRecipe(it) }

        return result.fold({ ex ->
            when {
                ex is NotFound -> prepareResponseWithText(
                    "Рецепт с идентификатором $textId не найден",
                    telegramAction
                )

                else -> prepareResponseWithText("Неизвестная ошибка: ${ex.message}", telegramAction)
            }
        },
            { rs ->
                prepareResponseWithText(generateRecipeMessage(rs), telegramAction)
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