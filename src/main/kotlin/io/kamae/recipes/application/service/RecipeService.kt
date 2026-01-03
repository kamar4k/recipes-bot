package io.kamae.recipes.application.service

import io.kamae.recipes.application.dto.RecipeDto
import io.kamae.recipes.application.dto.RecipeShortInfoDto
import io.kamae.recipes.application.port.inbound.AddRecipeUseCase
import io.kamae.recipes.application.port.inbound.GetRecipeUseCase
import io.kamae.recipes.application.port.inbound.ListRecipesUseCase
import io.kamae.recipes.application.port.outbound.RecipeRepositoryPort
import io.kamae.recipes.domain.exception.RecipeNotFoundException
import org.springframework.stereotype.Service

@Service
class RecipeService(
    private val recipeRepositoryPort: RecipeRepositoryPort
): AddRecipeUseCase, GetRecipeUseCase, ListRecipesUseCase {
    override fun addRecipe(recipe: RecipeDto): RecipeDto {
       return recipeRepositoryPort.saveRecipe(recipe)
    }

    override fun getRecipeById(recipeId: String): RecipeDto {
        return recipeRepositoryPort.getRecipeById(recipeId)?: throw RecipeNotFoundException(recipeId)
    }

    override fun getRecipeInfoList(): List<RecipeShortInfoDto> {
        return recipeRepositoryPort.getRecipeInfoList()
    }
}