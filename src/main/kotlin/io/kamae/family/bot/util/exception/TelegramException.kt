package io.kamae.family.bot.util.exception

import io.kamae.family.bot.domain.telegram.dto.TelegramResponse

class TelegramException(val telegramResponse: TelegramResponse): RuntimeException(telegramResponse.text)