package io.kamae.recipes.application.port.outbound

import io.kamae.recipes.application.dto.RecipeDto
import io.kamae.recipes.application.dto.RecipeShortInfoDto
import java.util.UUID

interface RecipeRepositoryPort {
    fun getRecipeInfoList(): List<RecipeShortInfoDto>

    fun getRecipeById(id: UUID): RecipeDto?

    fun saveRecipe(recipe: RecipeDto): RecipeDto
}