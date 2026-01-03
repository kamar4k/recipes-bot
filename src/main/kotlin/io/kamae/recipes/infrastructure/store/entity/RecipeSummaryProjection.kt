package io.kamae.recipes.infrastructure.store.entity

import java.util.UUID

interface RecipeSummaryProjection {
    fun getId(): UUID
    fun getTitle(): String
}