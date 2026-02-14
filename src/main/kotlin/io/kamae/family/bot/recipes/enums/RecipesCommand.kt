package io.kamae.family.bot.recipes.enums

import io.kamae.family.bot.core.api.ActionService
import io.kamae.family.bot.core.api.TelegramBotCommand
import io.kamae.family.bot.recipes.service.AddRecipeActionService
import io.kamae.family.bot.recipes.service.GetRecipeActionService
import io.kamae.family.bot.recipes.service.ListRecipesActionService

enum class RecipesCommand(
    override val command: String,
    override val actionServiceClass: Class<out ActionService>,
    override val alias: String?,
    override val desc: String?
): TelegramBotCommand {
    ADD_RECIPE("/add-recipe", AddRecipeActionService::class.java, "Добавление рецепта", "Добавление рецепта"),
    GET_RECIPE("/get-recipe", GetRecipeActionService::class.java, null, "Получение рецепта. /get-recipe <идентификатор рецепта>"),
    LIST_RECIPES("/list-recipes", ListRecipesActionService::class.java, "Список рецептов", "Список рецептов"),
}