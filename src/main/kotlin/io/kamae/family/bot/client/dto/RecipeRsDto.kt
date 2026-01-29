package io.kamae.family.bot.client.dto

import java.util.*


data class RecipeRsDto(
    val id: UUID,
    val title: String,
    val ingredients: List<String>,
    val instructions: String,
    val author: String?
)