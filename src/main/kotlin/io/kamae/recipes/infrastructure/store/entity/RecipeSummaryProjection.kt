package io.kamae.recipes.infrastructure.store.entity

interface RecipeSummaryProjection {
    fun getId(): String
    fun getTitle(): String
}