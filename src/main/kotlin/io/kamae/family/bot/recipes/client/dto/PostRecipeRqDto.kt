package io.kamae.family.bot.recipes.client.dto

import java.util.*


data class PostRecipeRqDto(
    val id: UUID?,
    val title: String,
    val ingredients: List<String>,
    val instructions: String,
    val author: String?
)