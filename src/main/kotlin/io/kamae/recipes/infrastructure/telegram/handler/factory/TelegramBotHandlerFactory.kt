package io.kamae.recipes.infrastructure.telegram.handler.factory

import io.kamae.recipes.infrastructure.telegram.enums.TelegramBotCommand
import io.kamae.recipes.infrastructure.telegram.handler.TelegramBotHandler
import org.springframework.stereotype.Component
import org.springframework.util.ClassUtils

interface TelegramBotHandlerFactory {
    fun getHandler(command: String): TelegramBotHandler
}


@Component
class TelegramBotHandlerFactoryImpl(handlers: List<TelegramBotHandler>): TelegramBotHandlerFactory {
    private val handlersMap: Map<String, TelegramBotHandler> = init(handlers)

    private fun init(handlers: List<TelegramBotHandler>): Map<String, TelegramBotHandler> {
        val tempMap = mutableMapOf<String, TelegramBotHandler>()

        TelegramBotCommand.entries.map { command ->
            handlers.firstOrNull { ClassUtils.getUserClass(it.javaClass) == command.handlerClass} ?.run {
                tempMap.put(command.command, this)
            }
        }

        return tempMap.toMap()
    }

    override fun getHandler(command: String): TelegramBotHandler {
        return handlersMap[command] ?: error("Команда $command не найдена")
    }
}