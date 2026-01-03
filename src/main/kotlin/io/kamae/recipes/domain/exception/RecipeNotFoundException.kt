package io.kamae.recipes.domain.exception

class RecipeNotFoundException(recipeId: String): RuntimeException(
    "Рецепт с идентификатором '$recipeId' не найден"
) {
}