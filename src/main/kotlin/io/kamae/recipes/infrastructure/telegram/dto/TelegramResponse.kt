package io.kamae.recipes.infrastructure.telegram.dto

data class TelegramResponse(
    val text: String,
    val buttons: List<TelegramButton>? = null
)