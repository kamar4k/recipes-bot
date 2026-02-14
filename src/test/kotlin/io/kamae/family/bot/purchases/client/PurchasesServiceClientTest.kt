package io.kamae.family.bot.purchases.client

import com.github.tomakehurst.wiremock.client.WireMock.*
import feign.FeignException
import feign.RetryableException
import io.kamae.family.bot.AbstractClientTest
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import kotlin.test.assertEquals

class PurchasesServiceClientTest : AbstractClientTest() {
    companion object {
        private const val CREATE_PRODUCT_URL = "/v1/purchases"
        private const val GET_PRODUCTS_INFO_URL = "/v1/purchases"
        private val ADD_EVENT_URL = "/v1/purchases/$TEST_PRODUCT_ID"

        private const val ERROR_MESSAGE = "error message"

    }

    @Autowired
    private lateinit var purchasesServiceClient: PurchasesServiceClient

    @Test
    fun createProduct_success() {
        stubFor(post(CREATE_PRODUCT_URL).willReturn(ok()))

        purchasesServiceClient.createProduct(CREATE_PRODUCT_DTO)

        verify(
            postRequestedFor(urlEqualTo(CREATE_PRODUCT_URL))
                .withRequestBody(equalToJson(getTestResourcesAsString("createProductRq.json")))
        )
    }

    @Test
    fun createProduct_commonError() {
        stubFor(post(CREATE_PRODUCT_URL).willReturn(serverError().withBody(ERROR_MESSAGE)))

        val error = assertThrows<FeignException> { purchasesServiceClient.createProduct(CREATE_PRODUCT_DTO) }

        Assertions.assertEquals(500, error.status())
        Assertions.assertEquals(ERROR_MESSAGE, String(error.responseBody().get().array()))
    }

    @Test
    fun createProduct_timeout() {
        stubFor(post(CREATE_PRODUCT_URL).willReturn(serverError().withFixedDelay(2100)))

        assertThrows<RetryableException> { purchasesServiceClient.createProduct(CREATE_PRODUCT_DTO) }
    }

    @Test
    fun addProductEvent_success() {
        stubFor(put(ADD_EVENT_URL).willReturn(ok()))

        purchasesServiceClient.addProductEvent(ADD_EVENT_DTO, TEST_PRODUCT_ID)

        verify(
            putRequestedFor(urlEqualTo(ADD_EVENT_URL))
                .withRequestBody(equalToJson(getTestResourcesAsString("addEventRq.json")))
        )
    }

    @Test
    fun addProductEvent_commonError() {
        stubFor(put(ADD_EVENT_URL).willReturn(serverError().withBody(ERROR_MESSAGE)))

        val error =
            assertThrows<FeignException> { purchasesServiceClient.addProductEvent(ADD_EVENT_DTO, TEST_PRODUCT_ID) }

        Assertions.assertEquals(500, error.status())
        Assertions.assertEquals(ERROR_MESSAGE, String(error.responseBody().get().array()))
    }

    @Test
    fun addProductEvent_timeout() {
        stubFor(put(ADD_EVENT_URL).willReturn(serverError().withFixedDelay(1000)))

        assertThrows<RetryableException> { purchasesServiceClient.addProductEvent(ADD_EVENT_DTO, TEST_PRODUCT_ID) }
    }

    @Test
    fun getProductsInfo_success() {
        stubFor(get(GET_PRODUCTS_INFO_URL).willReturn(okJson(getTestResourcesAsString("getProductsRs.json"))))

        val result = purchasesServiceClient.getProductsInfo()

        verify(
            getRequestedFor(urlEqualTo(GET_PRODUCTS_INFO_URL))
        )

        assertEquals(GET_PRODUCTS_INFO_RS_DTO, result)
    }

    @Test
    fun getProductsInfo_commonError() {
        stubFor(get(GET_PRODUCTS_INFO_URL).willReturn(serverError().withBody(ERROR_MESSAGE)))

        val error =
            assertThrows<FeignException> { purchasesServiceClient.getProductsInfo() }

        Assertions.assertEquals(500, error.status())
        Assertions.assertEquals(ERROR_MESSAGE, String(error.responseBody().get().array()))
    }

    @Test
    fun getProductsInfo_timeout() {
        stubFor(get(GET_PRODUCTS_INFO_URL).willReturn(serverError().withFixedDelay(2100)))

        assertThrows<RetryableException> { purchasesServiceClient.getProductsInfo() }
    }
}