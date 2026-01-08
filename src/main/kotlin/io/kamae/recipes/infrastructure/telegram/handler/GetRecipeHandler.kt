package io.kamae.recipes.infrastructure.telegram.handler

import io.kamae.recipes.application.port.inbound.GetRecipeUseCase
import io.kamae.recipes.domain.exception.RecipeNotFoundException
import io.kamae.recipes.infrastructure.telegram.dto.TelegramResponse
import io.kamae.recipes.infrastructure.telegram.parser.TelegramMessageHandler
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Component
import java.util.UUID

@Component
@PreAuthorize("hasRole('READER')")
class GetRecipeHandler(
    private val getRecipeUseCase: GetRecipeUseCase,
    private val telegramMessageHandler: TelegramMessageHandler): TelegramBotHandler {
    override fun executeCommand(text: String?, chatId: Long): TelegramResponse {
        try {
            val uuid = UUID.fromString(text)
            val result = getRecipeUseCase.getRecipeById(uuid)

            return TelegramResponse(telegramMessageHandler.generateRecipeMessage(result), chatId)
        } catch (ex: RecipeNotFoundException) {
            return TelegramResponse("Рецепт не найден", chatId)
        }
    }
}