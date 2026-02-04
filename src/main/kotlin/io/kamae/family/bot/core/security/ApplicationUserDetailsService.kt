package io.kamae.family.bot.core.security

import io.kamae.family.bot.core.jpa.repository.ApplicationUserRepository
import io.kamae.family.bot.core.security.consts.AuthorizationConstants.GUEST_USER
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService

class ApplicationUserDetailsService(
    private val applicationUserRepository: ApplicationUserRepository
) : UserDetailsService {

    companion object {
        val logger: Logger = LoggerFactory.getLogger(ApplicationUserDetailsService::class.java)
    }

    override fun loadUserByUsername(username: String?): UserDetails {
        username ?: let {
            logger.debug("#userDetails username in null. Authorized as guest")
            return GUEST_USER
        }

        return applicationUserRepository.getByUsernameEquals(username)
            ?.let {
                logger.debug("#userDetails user {} successfully authorized with roles={}", username, it.role)
                ApplicationUser(it.username, it.role.split(","))
            }
            ?:let{
                logger.debug("#userDetails user {} not found and authorized as guest", username)
                GUEST_USER
            }
    }

}