package io.kamae.family.bot.recipes.enums

import io.kamae.family.bot.core.api.ActionService
import io.kamae.family.bot.core.api.TelegramBotCommand
import io.kamae.family.bot.core.service.DefaultActionService
import io.kamae.family.bot.core.service.HelpActionService
import io.kamae.family.bot.core.service.StartActionService
import io.kamae.family.bot.recipes.service.AddRecipeActionService
import io.kamae.family.bot.recipes.service.GetRecipeActionService
import io.kamae.family.bot.recipes.service.ListRecipesActionService

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
}