package io.kamae.family.bot.core.security

import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class AuthorizationUtils {
    fun getUserName(): String {
        return (SecurityContextHolder.getContext().authentication.principal as ApplicationUser).username
    }
}