package io.kamae.family.bot.core.api

import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import java.io.Serializable

interface TelegramBotMessageSender {
    fun <T:Serializable, Method:BotApiMethod<T>> sendMessage(method: Method): T
}