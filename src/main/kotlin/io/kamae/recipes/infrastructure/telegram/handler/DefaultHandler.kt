package io.kamae.recipes.infrastructure.telegram.handler

import io.kamae.recipes.infrastructure.telegram.dto.TelegramResponse
import org.springframework.stereotype.Component

@Component
class DefaultHandler: TelegramBotHandler {
    override fun executeCommand(text: String?): TelegramResponse {
        return TelegramResponse("Я Вас не понял. Для получения списка возможных команд введите /help")
    }
}