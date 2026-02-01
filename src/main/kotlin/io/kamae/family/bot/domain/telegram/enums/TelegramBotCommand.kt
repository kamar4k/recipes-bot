package io.kamae.family.bot.domain.telegram.enums

import io.kamae.family.bot.service.*
import io.kamae.family.bot.service.api.ActionService

enum class TelegramBotCommand(
    val command: String,
    val actionServiceClass: Class<out ActionService>,
    val alias: String?,
    val desc: String?
) {
    START("/start", StartActionService::class.java, "Главное меню", "Переход в главное меню"),
    ADD_RECIPE("/add", AddRecipeActionService::class.java, "Добавление рецепта", "Добавление рецепта"),
    GET_RECIPE("/get", GetRecipeActionService::class.java, null, "Получение рецепта. /get <идентификатор рецепта>"),
    LIST_RECIPES("/list", ListRecipesActionService::class.java, "Список рецептов", "Список рецептов"),
    HELP("/help", HelpActionService::class.java, "Помощь", "Список команд"),
    DEFAULT("/default", DefaultActionService::class.java, null, null);

    companion object {
        fun searchByAlias(alias: String) = TelegramBotCommand.entries.firstOrNull { it.alias == alias } ?: DEFAULT
    }
}