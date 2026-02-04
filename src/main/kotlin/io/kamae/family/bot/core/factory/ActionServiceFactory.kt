package io.kamae.family.bot.core.factory

import io.kamae.family.bot.core.api.ActionService
import org.springframework.stereotype.Component
import org.springframework.util.ClassUtils

interface ActionServiceFactory {
    fun getActionService(command: String): ActionService
}


@Component
class ActionServiceFactoryImpl(
    handlers: List<ActionService>,
    private val commandSetFactory: CommandSetFactory
): ActionServiceFactory {
    private val handlersMap: Map<String, ActionService> = init(handlers)

    private fun init(handlers: List<ActionService>): Map<String, ActionService> {
        val tempMap = mutableMapOf<String, ActionService>()

        commandSetFactory.getCommands().map { command ->
            handlers.firstOrNull { ClassUtils.getUserClass(it.javaClass) == command.actionServiceClass} ?.run {
                tempMap.put(command.command, this)
            }
        }

        return tempMap.toMap()
    }

    override fun getActionService(command: String): ActionService {
        return handlersMap[command] ?: error("Команда $command не найдена")
    }
}