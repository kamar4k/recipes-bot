package io.kamae.recipes.infrastructure.telegram.handler

import io.kamae.recipes.application.port.inbound.ListRecipesUseCase
import io.kamae.recipes.infrastructure.telegram.dto.TelegramButton
import io.kamae.recipes.infrastructure.telegram.dto.TelegramResponse
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Component

@Component
@PreAuthorize("hasRole('READER')")
class ListRecipesHandler(
    private val listRecipesUseCase: ListRecipesUseCase
) : TelegramBotHandler {
    override fun executeCommand(text: String?, chatId: Long): TelegramResponse {
        val results = listRecipesUseCase.getRecipeInfoList()

        return TelegramResponse("Выберите рецепт", chatId, results.map { TelegramButton(it.title, "/get ${it.id}") })
    }
}