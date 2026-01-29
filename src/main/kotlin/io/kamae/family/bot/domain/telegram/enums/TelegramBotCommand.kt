package io.kamae.family.bot.domain.telegram.enums

import io.kamae.family.bot.service.*
import io.kamae.family.bot.service.api.ActionService

enum class TelegramBotCommand(val command: String, val actionServiceClass: Class<out ActionService>, val desc: String?) {
    ADD_RECIPE("/add", AddRecipeActionService::class.java, "Добавление рецепта. /add Наименование\nИнгридиенты(каждый с новой строки)\n\nИнструкции"),
    GET_RECIPE("/get", GetRecipeActionService::class.java, "Получение рецепта. /get <идентификатор рецепта>"),
    LIST_RECIPES("/list", ListRecipesActionService::class.java, "Список рецептов"),
    HELP("/help", HelpActionService::class.java, "Список команд"),
    DEFAULT("/default", DefaultActionService::class.java, null),
}