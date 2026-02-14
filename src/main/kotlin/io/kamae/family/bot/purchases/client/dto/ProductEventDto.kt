package io.kamae.family.bot.purchases.client.dto

data class ProductEventDto (
    val changeType: ChangeType,
    val changeValue: Double
)