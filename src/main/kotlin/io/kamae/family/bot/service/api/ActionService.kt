package io.kamae.family.bot.service.api

import io.kamae.family.bot.domain.telegram.CommandContext
import io.kamae.family.bot.domain.telegram.TelegramActionResult
import io.kamae.family.bot.domain.telegram.dto.TelegramAction
import io.kamae.family.bot.domain.telegram.dto.TelegramResponse

interface ActionService {
    fun executeAndGetResult(telegramAction: TelegramAction): TelegramActionResult

    companion object {
        fun prepareResultWithText(
            text: String,
            action: TelegramAction,
            nextQuestion: CommandContext.Question? = null
        ): TelegramActionResult {
            return TelegramActionResult(
                TelegramResponse(
                    text,
                    action.telegramUserInfo.chatId
                ),
                nextQuestion
            )
        }
    }
}