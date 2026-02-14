package io.kamae.family.bot.purchases.client

import io.kamae.family.bot.purchases.client.dto.AddProductEventRqDto
import io.kamae.family.bot.purchases.client.dto.CreateProductRqDto
import io.kamae.family.bot.purchases.client.dto.GetProductsInfoRsDto
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.*
import java.util.*

@FeignClient(name = "purchases-service", url = "\${external-service.purchases.host}")
interface PurchasesServiceClient {
    @PostMapping("/v1/purchases")
    fun createProduct(@RequestBody request: CreateProductRqDto)

    @PutMapping("/v1/purchases/{productId}")
    fun addProductEvent(@RequestBody request: AddProductEventRqDto, @PathVariable productId: UUID)

    @GetMapping("/v1/purchases")
    fun getProductsInfo(): GetProductsInfoRsDto
}