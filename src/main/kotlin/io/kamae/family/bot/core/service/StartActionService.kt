package io.kamae.family.bot.core.service

import io.kamae.family.bot.core.api.ActionService
import io.kamae.family.bot.core.domain.model.TelegramAction
import io.kamae.family.bot.core.domain.model.TelegramActionResult
import io.kamae.family.bot.core.domain.model.TelegramResponse
import io.kamae.family.bot.recipes.domain.keyboard.BaseKeyboard
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
@PreAuthorize("hasRole('GUEST')")
class StartActionService : ActionService {

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