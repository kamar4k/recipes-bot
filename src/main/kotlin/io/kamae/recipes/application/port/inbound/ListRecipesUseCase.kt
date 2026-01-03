package io.kamae.recipes.application.port.inbound

import io.kamae.recipes.application.dto.RecipeShortInfoDto

interface ListRecipesUseCase {
    fun getRecipeInfoList(): List<RecipeShortInfoDto>
}