package io.kamae.family.bot.domain.telegram.dto

import io.kamae.family.bot.domain.telegram.CommandContext

data class TelegramAction(val commandContext: CommandContext, val telegramUserInfo: TelegramUserInfo) {
    constructor(commandContext: CommandContext, chatIt: Long, username: String) : this(
        commandContext, TelegramUserInfo(chatIt, username)
    )
}