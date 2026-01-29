package io.kamae.family.bot.jpa.entity

import io.kamae.family.bot.security.UserRole
import jakarta.persistence.*
import java.util.*

@Entity
@Table(name = "bot_user")
class ApplicationUserEntity(
    @Id
    @Column(name = "id")
    val id: UUID,
    @Column(name = "username")
    val username: String,
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    val role: UserRole
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