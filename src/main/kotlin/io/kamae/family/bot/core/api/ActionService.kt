package io.kamae.family.bot.core.api

import io.kamae.family.bot.core.domain.model.CommandContext
import io.kamae.family.bot.core.domain.model.TelegramActionResult
import io.kamae.family.bot.core.domain.model.TelegramAction
import io.kamae.family.bot.core.domain.model.TelegramResponse
import io.kamae.family.bot.core.api.BotKeyboard

interface ActionService {
    fun executeAndGetResult(telegramAction: TelegramAction): TelegramActionResult

    companion object {
        fun prepareResultWithText(
            text: String,
            action: TelegramAction,
            nextQuestion: CommandContext.Question? = null,
            keyboard: BotKeyboard? = null
        ): TelegramActionResult {
            return TelegramActionResult(
                TelegramResponse(
                    text,
                    action.telegramUserInfo.chatId,
                    keyboard = keyboard
                ),
                nextQuestion
            )
        }
    }
}