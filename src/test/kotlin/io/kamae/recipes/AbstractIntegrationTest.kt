package io.kamae.recipes

import com.ninjasquad.springmockk.SpykBean
import io.kamae.recipes.application.dto.RecipeDto
import io.kamae.recipes.application.dto.RecipeShortInfoDto
import io.kamae.recipes.infrastructure.store.entity.RecipeEntity
import io.kamae.recipes.infrastructure.store.repository.RecipeJpaRepository
import org.junit.jupiter.api.AfterEach
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [RecipesApplication::class])
abstract class AbstractIntegrationTest: AbstractTest() {
    companion object {
        const val TEST_CHAT_ID: Long = 1234L

        private const val TEST_RECIPE_INGREDIENTS_SERIALIZED = "ingridient1\ningridient2 3\ning3 200g"
        private const val TEST_ANOTHER_RECIPE_INSTRUCTIONS = "Step1\nStep2\nStep123"
        private val TEST_ANOTHER_RECIPE_INGREDIENTS = listOf("ingridient1", "ingridient2 3", "ing3 200g")
        private const val TEST_ANOTHER_RECIPE_INGREDIENTS_SERIALIZED = "ingridient1\ningridient2 3\ning3 200g"

        val TEST_CREATE_DATE: LocalDateTime = LocalDateTime.of(2026, 1, 2, 12, 54, 16)
        private val TEST_ANOTHER_CREATE_DATE: LocalDateTime = LocalDateTime.of(2026, 1, 3, 12, 54, 16)

        val TEST_RECIPE_ENTITY = RecipeEntity(
            TEST_RECIPE_ID,
            TEST_RECIPE_TITLE,
            TEST_RECIPE_INGREDIENTS_SERIALIZED,
            TEST_RECIPE_INSTRUCTIONS,
            TEST_CREATE_DATE
        )

        val TEST_ANOTHER_RECIPE_ENTITY = RecipeEntity(
            TEST_ANOTHER_RECIPE_ID,
            TEST_ANOTHER_RECIPE_TITLE,
            TEST_ANOTHER_RECIPE_INGREDIENTS_SERIALIZED,
            TEST_ANOTHER_RECIPE_INSTRUCTIONS,
            TEST_ANOTHER_CREATE_DATE
        )
    }

    @SpykBean
    protected lateinit var recipeJpaRepository: RecipeJpaRepository

    @AfterEach
    fun clearDB() {
        recipeJpaRepository.deleteAll()
    }
}