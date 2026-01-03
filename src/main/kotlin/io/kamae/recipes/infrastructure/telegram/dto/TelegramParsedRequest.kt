package io.kamae.recipes.infrastructure.telegram.dto

data class TelegramParsedRequest(val command: String, val text: String?)
