package io.kamae.recipes.infrastructure.store.adapter

import io.kamae.recipes.application.dto.RecipeDto
import io.kamae.recipes.application.dto.RecipeShortInfoDto
import io.kamae.recipes.application.port.outbound.RecipeRepositoryPort
import io.kamae.recipes.infrastructure.store.adapter.mapper.RecipeStoreMapper
import io.kamae.recipes.infrastructure.store.repository.RecipeJpaRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Component
import kotlin.jvm.optionals.getOrNull

@Component
class RecipeRepositoryAdapter(
    private val recipeJpaRepository: RecipeJpaRepository,
    private val recipeStoreMapper: RecipeStoreMapper
) : RecipeRepositoryPort {
    override fun getRecipeInfoList(): List<RecipeShortInfoDto> {
        val resultList = recipeJpaRepository.findAllSummary()

        return recipeStoreMapper.mapSummaryListToDto(resultList)
    }

    override fun getRecipeById(id: String): RecipeDto? {
        val result = recipeJpaRepository.findById(id).getOrNull()

        return recipeStoreMapper.mapEntityToDto(result)
    }

    @Transactional
    override fun saveRecipe(recipe: RecipeDto): RecipeDto {
        val entity = recipeStoreMapper.mapDtoToEntityWithGeneratedId(recipe)
        val saved = recipeJpaRepository.save(entity)

        return recipe.copy(id = saved.id)
    }
}