package io.kamae.family.bot.core.jpa.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.*

@Entity
@Table(name = "bot_user")
class ApplicationUserEntity(
    @Id
    @Column(name = "id")
    val id: UUID,
    @Column(name = "username")
    val username: String,

    @Column(name = "role")
    val role: String
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is ApplicationUserEntity) return false

        if (id != other.id) return false
        if (username != other.username) return false
        if (role != other.role) return false

        return true
    }

    override fun hashCode(): Int {
        var result = id.hashCode()
        result = 31 * result + username.hashCode()
        result = 31 * result + role.hashCode()
        return result
    }
}