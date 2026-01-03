package io.kamae.recipes.infrastructure.telegram.handler

import io.kamae.recipes.infrastructure.telegram.dto.TelegramResponse

interface TelegramBotHandler {
    fun executeCommand(text: String?): TelegramResponse
}