package io.kamae.family.bot.service

import io.kamae.family.bot.domain.telegram.TelegramActionResult
import io.kamae.family.bot.domain.telegram.dto.TelegramAction
import io.kamae.family.bot.service.api.ActionService
import io.kamae.family.bot.service.api.ActionService.Companion.prepareResultWithText
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