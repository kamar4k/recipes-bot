package io.kamae.family.bot.domain.telegram.enums

import io.kamae.family.bot.core.api.ActionService
import io.kamae.family.bot.core.api.TelegramBotCommand
import io.kamae.family.bot.service.*

enum class RecipesCommand(
    override val command: String,
    override val actionServiceClass: Class<out ActionService>,
    override val alias: String?,
    override val desc: String?
): TelegramBotCommand {
    START("/start", StartActionService::class.java, "Главное меню", "Переход в главное меню"),
    ADD_RECIPE("/add", AddRecipeActionService::class.java, "Добавление рецепта", "Добавление рецепта"),
    GET_RECIPE("/get", GetRecipeActionService::class.java, null, "Получение рецепта. /get <идентификатор рецепта>"),
    LIST_RECIPES("/list", ListRecipesActionService::class.java, "Список рецептов", "Список рецептов"),
    HELP("/help", HelpActionService::class.java, "Помощь", "Список команд"),
    DEFAULT("/default", DefaultActionService::class.java, null, null);

    companion object {
        fun searchByAlias(alias: String) = RecipesCommand.entries.firstOrNull { it.alias == alias } ?: DEFAULT
    }
}