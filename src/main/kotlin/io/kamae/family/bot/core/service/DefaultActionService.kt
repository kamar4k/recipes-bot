package io.kamae.family.bot.core.service

import io.kamae.family.bot.core.api.ActionService.Companion.prepareResultWithText
import io.kamae.family.bot.core.api.TelegramBotMessageSender
import io.kamae.family.bot.core.domain.model.TelegramAction
import io.kamae.family.bot.core.domain.model.TelegramActionResult
import org.springframework.stereotype.Service

@Service
class DefaultActionService(sender: TelegramBotMessageSender) : AbstractDefaultActionService(sender) {

    override fun executeAndGetResult(telegramAction: TelegramAction): TelegramActionResult {
        return prepareResultWithText(
            "Я Вас не понял. Для получения списка возможных команд введите /help", telegramAction
        )

    }
}