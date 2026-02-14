package io.kamae.family.bot.purchases.client.dto

import java.time.LocalDateTime
import java.util.*

data class ProductInfoDto(
    val id: UUID,
    val name: String,
    val quantity: Double,
    val predication: LocalDateTime?
)