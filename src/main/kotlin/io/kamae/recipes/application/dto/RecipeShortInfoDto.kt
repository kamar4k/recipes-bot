package io.kamae.recipes.application.dto

import java.util.UUID

data class RecipeShortInfoDto(
    val id: UUID,
    val title: String
)