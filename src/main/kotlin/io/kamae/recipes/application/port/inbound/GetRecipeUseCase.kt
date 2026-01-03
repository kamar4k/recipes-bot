package io.kamae.recipes.application.port.inbound

import io.kamae.recipes.application.dto.RecipeDto
import java.util.UUID

interface GetRecipeUseCase {
    fun getRecipeById(recipeId: UUID): RecipeDto
}