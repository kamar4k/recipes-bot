package io.kamae.family.bot.config

import io.kamae.family.bot.jpa.repository.ApplicationUserRepository
import io.kamae.family.bot.listener.delegate.TelegramBotDelegate
import io.kamae.family.bot.security.ApplicationUserDetailsService
import io.kamae.family.bot.security.UserRole
import io.kamae.family.bot.security.annotation.SecuredTelegramListener
import io.kamae.family.bot.security.aspect.SecuredTelegramListenerAspect
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.DependsOn
import org.springframework.context.annotation.EnableAspectJAutoProxy
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.security.access.hierarchicalroles.RoleHierarchy
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.core.userdetails.UserDetailsService


@Configuration
@EnableMethodSecurity(prePostEnabled = true, proxyTargetClass = true)
@EnableAspectJAutoProxy(proxyTargetClass = true)
class ApplicationSecurityConfig {
    @Bean
    fun roleHierarchy(): RoleHierarchy {
        val hierarchy = """
            ${UserRole.ROLE_ADMIN.name} > ${UserRole.ROLE_EDITOR.name}
            ${UserRole.ROLE_EDITOR.name} > ${UserRole.ROLE_READER.name}
            ${UserRole.ROLE_READER.name} > ${UserRole.ROLE_GUEST.name}
        """.trimIndent()

        return RoleHierarchyImpl.fromHierarchy(hierarchy)
    }

    @Bean
    fun authenticationManager(config: AuthenticationConfiguration): AuthenticationManager {
        return config.authenticationManager
    }

    @Bean
    fun userDetailsService(userRepository: ApplicationUserRepository): UserDetailsService {
        return ApplicationUserDetailsService(userRepository)
    }

    @Bean
    @DependsOn("authenticationManager")
    fun securedTelegramListenerAspect(
        authenticationManager: AuthenticationManager,
    ): SecuredTelegramListenerAspect {
        return SecuredTelegramListenerAspect(authenticationManager)
    }

    @EventListener(ContextRefreshedEvent::class)
    fun checkSecurityAnnotations(event: ContextRefreshedEvent) {
        val applicationContext = event.applicationContext
        val annotatedBeans = applicationContext.getBeansWithAnnotation(SecuredTelegramListener::class.java)

        val incorrectBeans = annotatedBeans.filter {
            it.value !is TelegramBotDelegate
        }

        if (incorrectBeans.isNotEmpty()) {
            val msgBuilder = StringBuilder(
                incorrectBeans.map { "${it.key} ${it.value::class.java.name}" }.joinToString(separator = "\n")
            )

            msgBuilder.insert(
                0,
                "Annotation ${SecuredTelegramListener::class.java.name} used in not TelegramBotDelegate implementations:\n"
            )

            error(msgBuilder.toString())
        }
    }
}