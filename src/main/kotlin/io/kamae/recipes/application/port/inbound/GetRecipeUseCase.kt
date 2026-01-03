package io.kamae.recipes.application.port.inbound

import io.kamae.recipes.application.dto.RecipeDto

interface GetRecipeUseCase {
    fun getRecipeById(recipeId: String): RecipeDto
}