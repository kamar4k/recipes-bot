package io.kamae.recipes.infrastructure.store.adapter

import com.ninjasquad.springmockk.SpykBean
import io.kamae.recipes.AbstractIntegrationTest
import io.kamae.recipes.infrastructure.util.UniqueValueGenerator
import io.mockk.every
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.junit.jupiter.api.assertNull
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class RecipeRepositoryAdapterTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var recipeRepositoryAdapter: RecipeRepositoryAdapter

    @SpykBean
    private lateinit var uniqueValueGenerator: UniqueValueGenerator

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
        every { uniqueValueGenerator.generateId() } returns TEST_RECIPE_ID
        every { uniqueValueGenerator.currentDateTime() } returns TEST_CREATE_DATE

        val result = recipeRepositoryAdapter.saveRecipe(TEST_RECIPE_DTO)

        assertNotNull(result.id)
        assertEquals(TEST_RECIPE_DTO_WITH_ID, result)

        val saved = recipeJpaRepository.findAll().firstOrNull()

        assertEquals(TEST_RECIPE_ENTITY, saved)
    }
}
