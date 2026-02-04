package io.kamae.family.bot.core.domain.model

data class TelegramActionResult(
    val telegramResponse: TelegramResponse,
    val nextQuestion: CommandContext.Question? = null
)