package io.kamae.recipes.infrastructure.telegram.enums

import io.kamae.recipes.infrastructure.telegram.handler.*

enum class TelegramBotCommand(val command: String, val handlerClass: Class<out TelegramBotHandler>, val desc: String?) {
    ADD_RECIPE("/add", AddRecipeHandler::class.java, "Добавление рецепта. /add Наименование\nИнгридиенты(каждый с новой строки)\n\nИнструкции"),
    GET_RECIPE("/get", GetRecipeHandler::class.java, "Получение рецепта. /get <идентификатор рецепта>"),
    LIST_RECIPES("/list", ListRecipesHandler::class.java, "Список рецептов"),
    HELP("/help", HelpHandler::class.java, "Список команд"),
    DEFAULT("/default", DefaultHandler::class.java, null),
}