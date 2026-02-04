package io.kamae.family.bot.core.security.consts

import io.kamae.family.bot.core.security.ApplicationUser
import io.kamae.family.bot.recipes.domain.RecipesUserRole
import org.springframework.security.core.userdetails.UserDetails

object AuthorizationConstants {
    private const val GUEST_USER_NAME = "GUEST"

    const val EMPTY_PASSWORD = "null"

    val GUEST_USER: UserDetails = ApplicationUser(GUEST_USER_NAME, listOf(RecipesUserRole.ROLE_RECIPES_GUEST.name))
}