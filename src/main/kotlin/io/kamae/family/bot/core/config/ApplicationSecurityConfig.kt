package io.kamae.family.bot.core.config

import io.kamae.family.bot.core.jpa.repository.ApplicationUserRepository
import io.kamae.family.bot.core.listener.delegate.TelegramBotDelegate
import io.kamae.family.bot.core.security.ApplicationUserDetailsService
import io.kamae.family.bot.core.security.annotation.SecuredTelegramListener
import io.kamae.family.bot.core.security.aspect.SecuredTelegramListenerAspect
import org.springframework.boot.autoconfigure.security.servlet.PathRequest
import org.springframework.context.annotation.*
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.SecurityFilterChain


@Configuration
@EnableMethodSecurity(prePostEnabled = true, proxyTargetClass = true)
@EnableAspectJAutoProxy(proxyTargetClass = true)
class ApplicationSecurityConfig {
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

    @Bean
    @Profile("h2")
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .authorizeHttpRequests {
                it.requestMatchers(PathRequest.toH2Console())
                    .permitAll()
                    .anyRequest()
                    .authenticated()
            }
            .csrf {it.ignoringRequestMatchers(PathRequest.toH2Console()) }
            .headers { headers -> headers.frameOptions { it.sameOrigin() } }
            .formLogin(withDefaults())

        return http.build()
    }
}