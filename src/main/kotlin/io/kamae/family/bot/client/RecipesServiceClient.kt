package io.kamae.family.bot.client

import io.kamae.family.bot.client.dto.ListRecipesRsDto
import io.kamae.family.bot.client.dto.PostRecipeRqDto
import io.kamae.family.bot.client.dto.RecipeRsDto
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import java.util.*

@FeignClient(name = "recipes-service", url = "\${external-service.recipes.host}")
interface RecipesServiceClient {
    @GetMapping("/v1/recipes")
    fun listRecipes(): ListRecipesRsDto

    @GetMapping("/v1/recipes/{recipeId}")
    fun getRecipe(@PathVariable recipeId: UUID): RecipeRsDto

    @PostMapping("/v1/recipes")
    fun addRecipe(postRecipeRqDto: PostRecipeRqDto)
}