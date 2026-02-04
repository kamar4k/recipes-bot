package io.kamae.family.bot.core.service

import io.kamae.family.bot.core.api.ActionService
import io.kamae.family.bot.core.api.ActionService.Companion.prepareResultWithText
import io.kamae.family.bot.core.domain.model.TelegramAction
import io.kamae.family.bot.core.domain.model.TelegramActionResult
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
@PreAuthorize("hasRole('GUEST')")
class DefaultActionService : ActionService {

    override fun executeAndGetResult(telegramAction: TelegramAction): TelegramActionResult {
        return prepareResultWithText(
            "Я Вас не понял. Для получения списка возможных команд введите /help", telegramAction
        )

    }
}