package io.kamae.recipes.infrastructure.store.repository

import io.kamae.recipes.infrastructure.store.entity.ApplicationUserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository("applicationUserRepository")
interface ApplicationUserRepository: JpaRepository<ApplicationUserEntity, UUID> {
    fun getByUsernameEquals(username: String): ApplicationUserEntity?
}