package io.kamae.family.bot.jpa.repository

import io.kamae.family.bot.jpa.entity.ApplicationUserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository("applicationUserRepository")
interface ApplicationUserRepository: JpaRepository<ApplicationUserEntity, UUID> {
    fun getByUsernameEquals(username: String): ApplicationUserEntity?
}