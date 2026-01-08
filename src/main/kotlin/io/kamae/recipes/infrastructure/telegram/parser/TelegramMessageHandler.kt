package io.kamae.recipes.infrastructure.telegram.parser

import arrow.core.Either
import io.kamae.recipes.application.dto.RecipeDto
import io.kamae.recipes.infrastructure.telegram.dto.TelegramParsedRequest
import io.kamae.recipes.infrastructure.telegram.dto.TelegramResponse
import org.springframework.stereotype.Component

@Component
class TelegramMessageHandler {

    companion object {
        private const val MESSAGE_PATTERN = "(?s)^/[a-z\\-]+((\\s.+)|$|(\\n.+))"
        private const val COMMAND_PATTERN = "^/[a-z\\-]+(?=\\s|$|\\n)"

        private const val INCORRECT_COMMAND_MSG = "Неверный формат команды, подробнее в /help"
        private const val MISSING_TEXT_MSG = "Отсутствует текстовое сообщение"
    }

    fun parseRecipe(text: String): RecipeDto {
        val title = text.substringBefore("\n")

        val next = text.substringAfter("\n")

        val ingredientsStr = next.substringBefore("\n\n")
        val ingredientsList = ingredientsStr.split("\n")

        val instructions = next.substringAfter("\n\n")

        return RecipeDto(null, title, ingredientsList, instructions)
    }

    fun generateRecipeMessage(recipeDto: RecipeDto): String {
        val sb = StringBuilder(recipeDto.title)

        sb.append("\nИнгридиенты:\n")
        sb.append(recipeDto.ingredients.joinToString(separator = "\n") { "- $it" })
        sb.append("\n\nИнструкции:\n")
        sb.append(recipeDto.instructions)

        return sb.toString()
    }

    fun parseTelegramMessage(text: String?, chatId: Long): Either<TelegramResponse, TelegramParsedRequest> {
            if (text.isNullOrBlank()) {
                return Either.Left(TelegramResponse(MISSING_TEXT_MSG, chatId))
            } else {
                if (!text.matches(Regex(MESSAGE_PATTERN))) {
                    return Either.Left(TelegramResponse(INCORRECT_COMMAND_MSG, chatId))
                } else {
                    val matchedCommand = Regex(COMMAND_PATTERN).find(text)!!

                    val command = matchedCommand.value
                    val data = if (command.length == text.length) {
                        null
                    } else {
                        text.substring(matchedCommand.range.last + 2)
                    }

                    return Either.Right(TelegramParsedRequest(command, data))
                }
            }
    }
}