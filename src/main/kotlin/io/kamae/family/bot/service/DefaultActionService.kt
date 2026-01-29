package io.kamae.family.bot.service

import io.kamae.family.bot.domain.telegram.dto.TelegramAction
import io.kamae.family.bot.domain.telegram.dto.TelegramResponse
import io.kamae.family.bot.service.api.ActionService
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
@PreAuthorize("hasRole('GUEST')")
class DefaultActionService : ActionService {

    override fun executeAndGetResponse(telegramAction: TelegramAction): TelegramResponse {
        return prepareResponseWithText(
            "Я Вас не понял. Для получения списка возможных команд введите /help",
            telegramAction
        )
    }
}