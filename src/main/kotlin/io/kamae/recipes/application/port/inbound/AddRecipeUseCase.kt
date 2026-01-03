package io.kamae.recipes.application.port.inbound

import io.kamae.recipes.application.dto.RecipeDto

interface AddRecipeUseCase {
    fun addRecipe(recipe: RecipeDto): RecipeDto
}