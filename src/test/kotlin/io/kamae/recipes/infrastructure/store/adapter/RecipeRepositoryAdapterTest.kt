package io.kamae.recipes.infrastructure.store.adapter

import io.kamae.recipes.AbstractIntegrationTest
import io.kamae.recipes.application.dto.RecipeDto
import io.kamae.recipes.application.dto.RecipeShortInfoDto
import io.mockk.every
import io.mockk.mockkStatic
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.assertNull
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime
import java.util.UUID
import kotlin.test.assertEquals

class RecipeRepositoryAdapterTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var recipeRepositoryAdapter: RecipeRepositoryAdapter

    @Test
    fun getRecipeInfoList_success() {
        recipeJpaRepository.save(TEST_RECIPE_ENTITY)
        recipeJpaRepository.save(TEST_ANOTHER_RECIPE_ENTITY)

        val result = recipeRepositoryAdapter.getRecipeInfoList()

        assertEquals(TEST_RECIPE_SHORT_INFO_LIST, result)
    }

    @Test
    fun getRecipeById_success() {
        recipeJpaRepository.save(TEST_RECIPE_ENTITY)

        val result = recipeRepositoryAdapter.getRecipeById(TEST_RECIPE_ID)

        assertEquals(TEST_RECIPE_DTO_WITH_ID, result)
    }

    @Test
    fun getRecipeById_notFound() {
        val result = recipeRepositoryAdapter.getRecipeById(TEST_RECIPE_ID)

        assertNull(result)
    }

    @Test
    fun saveRecipe_success() {
        var mockedUUID = UUID.fromString(TEST_RECIPE_ID)

        var result: RecipeDto? = null

        mockkStatic(UUID::class, LocalDateTime::class) {
            every { UUID.randomUUID() } returns mockedUUID
            every { LocalDateTime.now() } returns TEST_CREATE_DATE
            result = recipeRepositoryAdapter.saveRecipe(TEST_RECIPE_DTO)
        }
        assertNotNull(result?.id)
        assertEquals(TEST_RECIPE_DTO_WITH_ID, result)

        val saved = recipeJpaRepository.findAll().firstOrNull()

        assertEquals(TEST_RECIPE_ENTITY, saved)
    }
}
