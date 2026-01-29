package io.kamae.family.bot.service.api

import io.kamae.family.bot.domain.telegram.dto.TelegramAction
import io.kamae.family.bot.domain.telegram.dto.TelegramResponse

interface ActionService {
    fun executeAndGetResponse(telegramAction: TelegramAction): TelegramResponse

    fun prepareResponseWithText(text: String, action: TelegramAction): TelegramResponse {
        return TelegramResponse(
            text,
            action.telegramUserInfo.chatId
        )
    }
}