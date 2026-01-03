package io.kamae.recipes.application.dto

data class RecipeDto(
    val id: String?,
    val title: String,
    val ingredients: List<String>,
    val instructions: String
)