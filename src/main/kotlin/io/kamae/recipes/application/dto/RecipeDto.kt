package io.kamae.recipes.application.dto

import java.util.UUID

data class RecipeDto(
    val id: UUID?,
    val title: String,
    val ingredients: List<String>,
    val instructions: String
)