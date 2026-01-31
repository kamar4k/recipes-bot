package io.kamae.family.bot.domain.telegram

import io.kamae.family.bot.domain.telegram.dto.TelegramResponse

data class TelegramActionResult(
    val telegramResponse: TelegramResponse,
    val nextQuestion: CommandContext.Question? = null
)