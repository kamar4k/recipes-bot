package io.kamae.family.bot.purchases.enums

import io.kamae.family.bot.core.api.ActionService
import io.kamae.family.bot.core.api.TelegramBotCommand
import io.kamae.family.bot.purchases.service.CreateProductActionService
import io.kamae.family.bot.purchases.service.IncreaseProductValueActionService
import io.kamae.family.bot.purchases.service.ListProductsActionService
import io.kamae.family.bot.purchases.service.ReduceProductValueActionService

enum class PurchasesCommand(
    override val command: String,
    override val actionServiceClass: Class<out ActionService>,
    override val alias: String?,
    override val desc: String?
) : TelegramBotCommand {
    CREATE_PRODUCT(
        "/create-product",
        CreateProductActionService::class.java,
        "Добавление продукта",
        "Добавление продукта"
    ),
    LIST_PRODUCTS("/list-products", ListProductsActionService::class.java, "Список продуктов", "Список продуктов"),
    REDUCE_PRODUCT("/reduce-product", ReduceProductValueActionService::class.java, null, "Уменьнить кол-во продукта"),
    INCREASE_PRODUCT(
        "/increase-product",
        IncreaseProductValueActionService::class.java,
        null,
        "Увеличить кол-во продукта"
    ),
}