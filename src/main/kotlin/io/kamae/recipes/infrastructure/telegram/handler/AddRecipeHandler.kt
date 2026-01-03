package io.kamae.recipes.infrastructure.telegram.handler

import io.kamae.recipes.application.port.inbound.AddRecipeUseCase
import io.kamae.recipes.infrastructure.telegram.dto.TelegramResponse
import io.kamae.recipes.infrastructure.telegram.parser.TelegramMessageHandler
import org.springframework.stereotype.Component

@Component
class AddRecipeHandler(
    private val telegramMessageParser: TelegramMessageHandler,
    private val addRecipeUseCase: AddRecipeUseCase
): TelegramBotHandler {
    override fun executeCommand(text: String?): TelegramResponse {
        val parsedRecipe = telegramMessageParser.parseRecipe(checkNotNull(text))
        val saved = addRecipeUseCase.addRecipe(parsedRecipe)

        return TelegramResponse("Рецепт ${saved.title} добавлен с идентификатором ${saved.id}")
    }
}