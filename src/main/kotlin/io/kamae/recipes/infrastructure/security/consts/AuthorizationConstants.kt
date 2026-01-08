package io.kamae.recipes.infrastructure.security.consts

import io.kamae.recipes.infrastructure.security.ApplicationUser
import io.kamae.recipes.infrastructure.security.UserRole
import org.springframework.security.core.userdetails.UserDetails

object AuthorizationConstants {
    private const val GUEST_USER_NAME = "GUEST"

    const val EMPTY_PASSWORD = "null"

    val GUEST_USER: UserDetails = ApplicationUser(GUEST_USER_NAME, UserRole.ROLE_GUEST)
}