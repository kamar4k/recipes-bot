package io.kamae.recipes.application.port.outbound

import io.kamae.recipes.application.dto.RecipeDto
import io.kamae.recipes.application.dto.RecipeShortInfoDto

interface RecipeRepositoryPort {
    fun getRecipeInfoList(): List<RecipeShortInfoDto>

    fun getRecipeById(id: String): RecipeDto?

    fun saveRecipe(recipe: RecipeDto): RecipeDto
}