package io.kamae.family.bot.security

import io.kamae.family.bot.security.consts.AuthorizationConstants.EMPTY_PASSWORD
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder

class ApplicationUser(
    private val username: String,
    role: UserRole
): UserDetails {
    companion object {
        private val passwordEncoder: PasswordEncoder = PasswordEncoderFactories.createDelegatingPasswordEncoder()
    }

    private val authorities = mutableListOf(SimpleGrantedAuthority(role.name))

    override fun getAuthorities(): MutableCollection<out GrantedAuthority> {
        return authorities
    }

    override fun getPassword(): String {
        return passwordEncoder.encode(EMPTY_PASSWORD)
    }

    override fun getUsername(): String {
        return username
    }
}