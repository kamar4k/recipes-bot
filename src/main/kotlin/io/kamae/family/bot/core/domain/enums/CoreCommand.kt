package io.kamae.family.bot.core.domain.enums

import io.kamae.family.bot.core.api.ActionService
import io.kamae.family.bot.core.api.TelegramBotCommand
import io.kamae.family.bot.core.service.DefaultActionService
import io.kamae.family.bot.core.service.HelpActionService
import io.kamae.family.bot.core.service.StartActionService

enum class CoreCommand(
    override val command: String,
    override val actionServiceClass: Class<out ActionService>,
    override val alias: String?,
    override val desc: String?
): TelegramBotCommand {
    START("/start", StartActionService::class.java, "Главное меню", "Переход в главное меню"),
    HELP("/help", HelpActionService::class.java, "Помощь", "Список команд"),
    DEFAULT("/default", DefaultActionService::class.java, null, null);
}