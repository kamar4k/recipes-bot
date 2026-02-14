package io.kamae.family.bot.purchases.client.dto

data class CreateProductRqDto(
    val productName: String,
    val event: ProductEventDto
)
