package io.kamae.family.bot.core.service

import io.kamae.family.bot.core.api.TelegramBotMessageSender
import io.kamae.family.bot.core.domain.model.TelegramAction
import io.kamae.family.bot.core.domain.model.TelegramActionResult
import io.kamae.family.bot.core.domain.model.TelegramResponse
import io.kamae.family.bot.recipes.domain.keyboard.BaseKeyboard
import org.springframework.stereotype.Service

@Service
class StartActionService(sender: TelegramBotMessageSender) : AbstractDefaultActionService(sender) {

    override fun executeAndGetResult(telegramAction: TelegramAction): TelegramActionResult {
        return TelegramActionResult(
            TelegramResponse(
                "Введите команду или нажмите на кнопку",
                telegramAction.telegramUserInfo.chatId,
                keyboard = BaseKeyboard
            )
        )
    }
}