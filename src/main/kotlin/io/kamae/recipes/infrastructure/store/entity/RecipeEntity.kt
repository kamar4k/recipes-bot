package io.kamae.recipes.infrastructure.store.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime
import java.util.*

@Entity
@Table(name = "recipe")
class RecipeEntity(
    @Id
    @Column(name = "id")
    val id: UUID,
    @Column(name = "title")
    val title: String,
    @Column(name = "ingredients")
    val ingredients: String,
    @Column(name = "instructions")
    val instructions: String,
    @Column(name = "created_at")
    val createdAt: LocalDateTime = LocalDateTime.now()
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is RecipeEntity) return false

        if (id != other.id) return false
        if (title != other.title) return false
        if (ingredients != other.ingredients) return false
        if (instructions != other.instructions) return false
        if (createdAt != other.createdAt) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + title.hashCode()
        result = 31 * result + ingredients.hashCode()
        result = 31 * result + instructions.hashCode()
        result = 31 * result + createdAt.hashCode()
        return result
    }
}