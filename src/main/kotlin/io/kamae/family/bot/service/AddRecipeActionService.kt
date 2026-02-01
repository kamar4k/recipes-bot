package io.kamae.family.bot.service

import arrow.core.Either
import feign.RetryableException
import io.kamae.family.bot.client.RecipesServiceClient
import io.kamae.family.bot.client.dto.PostRecipeRqDto
import io.kamae.family.bot.core.domain.model.CommandContext
import io.kamae.family.bot.core.domain.model.TelegramActionResult
import io.kamae.family.bot.core.domain.model.TelegramAction
import io.kamae.family.bot.core.domain.model.TelegramResponse
import io.kamae.family.bot.domain.telegram.keyboard.BaseKeyboard
import io.kamae.family.bot.domain.telegram.keyboard.CancellationKeyboard
import io.kamae.family.bot.core.api.ActionService
import io.kamae.family.bot.core.api.ActionService.Companion.prepareResultWithText
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
@PreAuthorize("hasRole('EDITOR')")
class AddRecipeActionService(private val recipesServiceClient: RecipesServiceClient) : ActionService {
    companion object {
        private const val CANCELLATION_MESSAGE = "Для отмены нажмите кнопку или введите 'Отменить'"
    }

    override fun executeAndGetResult(telegramAction: TelegramAction): TelegramActionResult {
        val lastContextElement = telegramAction.commandContext
            .sequence
            .lastOrNull()
            ?.also {
                if (it.answer?.value == "Отмена") return prepareResultWithText(
                    "Ввод рецепта отменён",
                    telegramAction,
                    keyboard = BaseKeyboard
                )
            }
            ?: return prepareNameInput(telegramAction)

        return when (AddRecipeQuestion.valueOf(lastContextElement.question.value)) {
            AddRecipeQuestion.INPUT_NAME -> prepareIngredientInput(telegramAction)
            AddRecipeQuestion.INPUT_INGREDIENTS -> prepareInstructionsInput(telegramAction)

            AddRecipeQuestion.INPUT_INSTRUCTIONS -> saveRecipeAndPrepareResponse(telegramAction)
        }
    }

    private fun saveRecipeAndPrepareResponse(telegramAction: TelegramAction): TelegramActionResult {
        val recipe = parseRecipe(telegramAction.commandContext, telegramAction.telegramUserInfo.username)

        val result = Either.catch { recipesServiceClient.addRecipe(recipe) }

        return result.fold(
            {
                when {
                    it is RetryableException -> prepareResultWithText(
                        "Сервис недоступен",
                        telegramAction,
                        keyboard = BaseKeyboard
                    )

                    else -> prepareResultWithText(
                        "Неизвестная ошибка: ${it.message}",
                        telegramAction,
                        keyboard = BaseKeyboard
                    )
                }
            },
            {
                TelegramActionResult(
                    TelegramResponse(
                        "Рецепт успешно добавлен",
                        telegramAction.telegramUserInfo.chatId,
                        keyboard = BaseKeyboard
                    )
                )
            }
        )
    }

    private fun prepareNameInput(telegramAction: TelegramAction): TelegramActionResult {
        return prepareResultWithText(
            "Введите наименование рецепта. $CANCELLATION_MESSAGE",
            telegramAction,
            AddRecipeQuestion.INPUT_NAME.asQuestion(),
            CancellationKeyboard
        )
    }

    private fun prepareIngredientInput(telegramAction: TelegramAction): TelegramActionResult {
        return prepareResultWithText(
            "Введите ингредиенты (каждый на новой строке). $CANCELLATION_MESSAGE",
            telegramAction,
            AddRecipeQuestion.INPUT_INGREDIENTS.asQuestion(),
            CancellationKeyboard
        )
    }

    private fun prepareInstructionsInput(telegramAction: TelegramAction): TelegramActionResult {
        return prepareResultWithText(
            "Введите инструкции по приготовлению. $CANCELLATION_MESSAGE",
            telegramAction,
            AddRecipeQuestion.INPUT_INSTRUCTIONS.asQuestion(),
            CancellationKeyboard
        )
    }


    private fun parseRecipe(commandContext: CommandContext, author: String): PostRecipeRqDto {
        val title =
            commandContext.sequence.first { AddRecipeQuestion.INPUT_NAME.isQuestion(it.question) }.answer!!.value

        val ingredientsList =
            commandContext.sequence.first { AddRecipeQuestion.INPUT_INGREDIENTS.isQuestion(it.question) }
                .answer!!.value.split("\n")

        val instructions =
            commandContext.sequence.first { AddRecipeQuestion.INPUT_INSTRUCTIONS.isQuestion(it.question) }.answer!!.value

        return PostRecipeRqDto(null, title, ingredientsList, instructions, author)
    }

    private enum class AddRecipeQuestion {
        INPUT_NAME,
        INPUT_INGREDIENTS,
        INPUT_INSTRUCTIONS;

        fun asQuestion() = CommandContext.Question(this.name)
        fun isQuestion(question: CommandContext.Question) = this.name == question.value
    }
}