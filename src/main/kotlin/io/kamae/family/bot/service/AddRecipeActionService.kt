package io.kamae.family.bot.service

import arrow.core.Either
import feign.RetryableException
import io.kamae.family.bot.client.RecipesServiceClient
import io.kamae.family.bot.client.dto.PostRecipeRqDto
import io.kamae.family.bot.domain.telegram.CommandContext
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
        val lastContextElement = telegramAction.commandContext
            .sequence
            .lastOrNull()
            ?.also {
                if (it.answer?.value == "/cancel") return prepareResultWithText(
                    "Ввод рецепта отменён",
                    telegramAction
                )
            }
            ?: return prepareNameInput(telegramAction)

        return when (AddRecipeQuestion.valueOf(lastContextElement.question.value)) {
            AddRecipeQuestion.INPUT_NAME -> prepareIngredientInput(telegramAction)
            AddRecipeQuestion.INPUT_INGREDIENT -> if (lastContextElement.answer?.value != "/ready") prepareIngredientInput(
                telegramAction
            ) else prepareInstructionsInput(telegramAction)

            AddRecipeQuestion.INPUT_INSTRUCTIONS -> saveRecipeAndPrepareResponse(telegramAction)
        }
    }

    private fun saveRecipeAndPrepareResponse(telegramAction: TelegramAction): TelegramActionResult {
        val recipe = parseRecipe(telegramAction.commandContext, telegramAction.telegramUserInfo.username)

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

    private fun prepareNameInput(telegramAction: TelegramAction): TelegramActionResult {
        return prepareResultWithText(
            "Введите наименование рецепта. /cancel - отменить",
            telegramAction,
            AddRecipeQuestion.INPUT_NAME.asQuestion()
        )
    }

    private fun prepareIngredientInput(telegramAction: TelegramAction): TelegramActionResult {
        return prepareResultWithText(
            "Введите ингредиент. /cancel - отменить, /ready - завершить ввод",
            telegramAction,
            AddRecipeQuestion.INPUT_INGREDIENT.asQuestion()
        )
    }

    private fun prepareInstructionsInput(telegramAction: TelegramAction): TelegramActionResult {
        return prepareResultWithText(
            "Введите инструкции по приготовлению. /cancel - отменить",
            telegramAction,
            AddRecipeQuestion.INPUT_INSTRUCTIONS.asQuestion()
        )
    }


    private fun parseRecipe(commandContext: CommandContext, author: String): PostRecipeRqDto {
        val title =
            commandContext.sequence.first { AddRecipeQuestion.INPUT_NAME.isQuestion(it.question) }.answer!!.value

        val ingredientsList =
            commandContext.sequence.filter { AddRecipeQuestion.INPUT_INGREDIENT.isQuestion(it.question) && it.answer?.value != "/ready" }
                .mapNotNull { it.answer?.value }

        val instructions =
            commandContext.sequence.first { AddRecipeQuestion.INPUT_INSTRUCTIONS.isQuestion(it.question) }.answer!!.value

        return PostRecipeRqDto(null, title, ingredientsList, instructions, author)
    }

    private enum class AddRecipeQuestion {
        INPUT_NAME,
        INPUT_INGREDIENT,
        INPUT_INSTRUCTIONS;

        fun asQuestion() = CommandContext.Question(this.name)
        fun isQuestion(question: CommandContext.Question) = this.name == question.value
    }
}