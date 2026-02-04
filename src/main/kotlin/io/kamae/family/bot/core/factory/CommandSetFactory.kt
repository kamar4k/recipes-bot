package io.kamae.family.bot.core.factory

import io.kamae.family.bot.core.api.TelegramBotCommand
import io.kamae.family.bot.core.domain.enums.CoreCommand
import org.springframework.stereotype.Component

interface CommandSetFactory {
    fun searchByAlias(alias: String): TelegramBotCommand

    fun getCommands(): Collection<TelegramBotCommand>
}

@Component
class CommandSetFactoryImpl(
    commandRegisters: Collection<CommandRegister>
) : CommandSetFactory {
    private val commands: List<TelegramBotCommand> = buildList {
        addAll(CoreCommand.entries)
        addAll(commandRegisters.flatMap { it.getCommands() })
    }.toList()

    override fun searchByAlias(alias: String) =
        commands.firstOrNull { it.alias == alias } ?: CoreCommand.DEFAULT

    override fun getCommands(): Collection<TelegramBotCommand> {
        return commands
    }
}