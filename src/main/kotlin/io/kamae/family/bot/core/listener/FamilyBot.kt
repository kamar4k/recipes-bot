package io.kamae.family.bot.core.listener

import io.kamae.family.bot.core.api.TelegramBotMessageSender
import io.kamae.family.bot.core.config.TelegramBotConfig
import io.kamae.family.bot.core.domain.model.TelegramUpdateEvent
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import org.telegram.telegrambots.meta.api.objects.Update
import java.io.Serializable

@Component
class FamilyBot(
    private val telegramBotConfig: TelegramBotConfig,
    private val applicationEventPublisher: ApplicationEventPublisher
) : TelegramLongPollingBot(telegramBotConfig.token), TelegramBotMessageSender {
    override fun getBotUsername(): String = telegramBotConfig.name
    override fun onUpdateReceived(update: Update?) {
        if (update == null) return

        applicationEventPublisher.publishEvent(TelegramUpdateEvent(update))
    }

    override fun <T : Serializable, Method : BotApiMethod<T>> sendMessage(method: Method): T {
        return execute(method)
    }
}