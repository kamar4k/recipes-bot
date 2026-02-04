package io.kamae.family.bot.core.exception

import io.kamae.family.bot.core.domain.model.TelegramResponse

class TelegramException(val telegramResponse: TelegramResponse): RuntimeException(telegramResponse.text)