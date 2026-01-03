package io.kamae.recipes.infrastructure.telegram.handler

import io.kamae.recipes.application.port.inbound.GetRecipeUseCase
import io.kamae.recipes.domain.exception.RecipeNotFoundException
import io.kamae.recipes.infrastructure.telegram.dto.TelegramResponse
import io.kamae.recipes.infrastructure.telegram.parser.TelegramMessageHandler
import org.springframework.stereotype.Component

@Component
class GetRecipeHandler(
    private val getRecipeUseCase: GetRecipeUseCase,
    private val telegramMessageHandler: TelegramMessageHandler): TelegramBotHandler {
    override fun executeCommand(text: String?): TelegramResponse {
        try {
            val result = getRecipeUseCase.getRecipeById(checkNotNull(text))

            return TelegramResponse(telegramMessageHandler.generateRecipeMessage(result))
        } catch (ex: RecipeNotFoundException) {
            return TelegramResponse("Рецепт не найден")
        }
    }
}