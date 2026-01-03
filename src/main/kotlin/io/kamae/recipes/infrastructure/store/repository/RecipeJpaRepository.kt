package io.kamae.recipes.infrastructure.store.repository

import io.kamae.recipes.infrastructure.store.entity.RecipeEntity
import io.kamae.recipes.infrastructure.store.entity.RecipeSummaryProjection
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface RecipeJpaRepository: JpaRepository<RecipeEntity, UUID> {

    @Query(
        value = "SELECT recipe.id as id, recipe.title as title FROM RecipeEntity recipe ORDER BY recipe.createdAt ASC"
    )
    fun findAllSummary(): List<RecipeSummaryProjection>
}