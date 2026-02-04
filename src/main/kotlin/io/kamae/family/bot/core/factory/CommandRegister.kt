package io.kamae.family.bot.core.factory

import io.kamae.family.bot.core.api.TelegramBotCommand

fun interface CommandRegister {
    fun getCommands(): Collection<TelegramBotCommand>
}