package io.kamae.family.bot.core.domain.model

data class TelegramAction(val commandContext: CommandContext, val telegramUserInfo: TelegramUserInfo) {
    constructor(commandContext: CommandContext, chatIt: Long, username: String) : this(
        commandContext, TelegramUserInfo(chatIt, username)
    )
}