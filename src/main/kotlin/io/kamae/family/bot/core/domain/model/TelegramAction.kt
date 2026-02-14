package io.kamae.family.bot.core.domain.model

data class TelegramAction(
    val commandContext: CommandContext,
    val telegramUserInfo: TelegramUserInfo,
    val messageId: Int? = null
) {
    fun getChatId() = telegramUserInfo.chatId
}