package io.kamae.recipes.infrastructure.telegram.handler

import io.kamae.recipes.infrastructure.telegram.dto.TelegramResponse
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Component

@Component
@PreAuthorize("hasRole('GUEST')")
class DefaultHandler: TelegramBotHandler {
    override fun executeCommand(text: String?, chatId: Long): TelegramResponse {
        return TelegramResponse("Я Вас не понял. Для получения списка возможных команд введите /help", chatId)
    }
}