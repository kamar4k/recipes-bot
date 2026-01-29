package io.kamae.family.bot.client.dto

import java.util.*


data class PostRecipeRqDto(
    val id: UUID?,
    val title: String,
    val ingredients: List<String>,
    val instructions: String,
    val author: String?
)