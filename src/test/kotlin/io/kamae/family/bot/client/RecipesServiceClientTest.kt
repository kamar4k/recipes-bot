package io.kamae.family.bot.client

import com.github.tomakehurst.wiremock.client.WireMock.*
import feign.FeignException
import feign.RetryableException
import io.kamae.family.bot.AbstractClientTest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired

class RecipesServiceClientTest : AbstractClientTest() {
    companion object {
        private const val ERROR_MESSAGE = "error message"
    }

    @Autowired
    private lateinit var recipesServiceClient: RecipesServiceClient

    @Test
    fun listRecipes_success() {
        stubFor(
            get("/v1/recipes")
                .willReturn(okJson(getTestResourcesAsString("listRecipesRs.json")))
        )

        val result = recipesServiceClient.listRecipes()

        verify(
            getRequestedFor(urlEqualTo("/v1/recipes"))
        )

        assertEquals(TEST_RECIPES_LIST_RS, result)
    }

    @Test
    fun listRecipes_commonError() {
        stubFor(
            get("/v1/recipes")
                .willReturn(serverError().withBody(ERROR_MESSAGE))
        )

        val error = assertThrows<FeignException> { recipesServiceClient.listRecipes() }

        verify(
            getRequestedFor(urlEqualTo("/v1/recipes"))
        )

        assertEquals(500, error.status())
        assertEquals(ERROR_MESSAGE, String(error.responseBody().get().array()))
    }

    @Test
    fun listRecipes_timeout() {
        stubFor(
            get("/v1/recipes")
                .willReturn(serverError().withFixedDelay(1000))
        )

        assertThrows<RetryableException> { recipesServiceClient.listRecipes() }

        verify(
            getRequestedFor(urlEqualTo("/v1/recipes"))
        )
    }

    @Test
    fun getRecipe_success() {
        stubFor(
            get("/v1/recipes/${TEST_RECIPE_ID}")
                .willReturn(okJson(getTestResourcesAsString("getRecipeRs.json")))
        )

        val result = recipesServiceClient.getRecipe(TEST_RECIPE_ID)

        verify(
            getRequestedFor(urlEqualTo("/v1/recipes/${TEST_RECIPE_ID}"))
        )

        assertEquals(TEST_RECIPE_DTO_WITH_ID, result)
    }

    @Test
    fun getRecipe_notFound() {
        stubFor(
            get("/v1/recipes/${TEST_RECIPE_ID}")
                .willReturn(notFound())
        )

        val error = assertThrows<FeignException> { recipesServiceClient.getRecipe(TEST_RECIPE_ID) }

        verify(
            getRequestedFor(urlEqualTo("/v1/recipes/${TEST_RECIPE_ID}"))
        )

        assertEquals(404, error.status())
    }

    @Test
    fun getRecipe_commonError() {
        stubFor(
            get("/v1/recipes/${TEST_RECIPE_ID}")
                .willReturn(serverError().withBody(ERROR_MESSAGE))
        )

        val error = assertThrows<FeignException> { recipesServiceClient.getRecipe(TEST_RECIPE_ID) }

        verify(
            getRequestedFor(urlEqualTo("/v1/recipes/${TEST_RECIPE_ID}"))
        )

        assertEquals(500, error.status())
        assertEquals(ERROR_MESSAGE, String(error.responseBody().get().array()))
    }

    @Test
    fun getRecipe_timeout() {
        stubFor(
            get("/v1/recipes/${TEST_RECIPE_ID}")
                .willReturn(serverError().withFixedDelay(1000))
        )

        assertThrows<RetryableException> { recipesServiceClient.getRecipe(TEST_RECIPE_ID) }

        verify(
            getRequestedFor(urlEqualTo("/v1/recipes/${TEST_RECIPE_ID}"))
        )
    }

    @Test
    fun addRecipe_success() {
        stubFor(post("/v1/recipes").willReturn(ok()))

        recipesServiceClient.addRecipe(TEST_RECIPE_DTO)

        verify(
            postRequestedFor(
                urlEqualTo("/v1/recipes")
            ).withRequestBody(equalToJson(getTestResourcesAsString("addRecipeRq.json")))
        )
    }

    @Test
    fun addRecipe_commonError() {
        stubFor(post("/v1/recipes").willReturn(serverError().withBody(ERROR_MESSAGE)))

        val error = assertThrows<FeignException> { recipesServiceClient.addRecipe(TEST_RECIPE_DTO) }

        verify(
            postRequestedFor(
                urlEqualTo("/v1/recipes")
            ).withRequestBody(equalToJson(getTestResourcesAsString("addRecipeRq.json")))
        )

        assertEquals(500, error.status())
        assertEquals(ERROR_MESSAGE, String(error.responseBody().get().array()))
    }

    @Test
    fun addRecipe_timeout() {
        stubFor(post("/v1/recipes").willReturn(serverError().withFixedDelay(1000)))

        assertThrows<RetryableException> { recipesServiceClient.addRecipe(TEST_RECIPE_DTO) }

        verify(
            postRequestedFor(
                urlEqualTo("/v1/recipes")
            ).withRequestBody(equalToJson(getTestResourcesAsString("addRecipeRq.json")))
        )
    }
}