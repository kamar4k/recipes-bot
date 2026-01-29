package io.kamae.family.bot.domain.telegram.dto

data class TelegramAction(val telegramParsedRequest: TelegramParsedRequest, val telegramUserInfo: TelegramUserInfo) {
    constructor(command: String, text: String?, chatIt: Long, username: String) : this(
        TelegramParsedRequest(
            command,
            text
        ), TelegramUserInfo(chatIt, username)
    )
}