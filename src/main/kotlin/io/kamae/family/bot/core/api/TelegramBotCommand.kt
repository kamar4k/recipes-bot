package io.kamae.family.bot.core.api

interface TelegramBotCommand {
    val command: String
    val actionServiceClass: Class<out ActionService>
    val alias: String?
    val desc: String?
}