package io.kamae.recipes.infrastructure.config

import io.kamae.recipes.AbstractTest
import io.kamae.recipes.infrastructure.security.annotation.SecuredTelegramListener
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.springframework.boot.test.context.runner.ApplicationContextRunner
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import testinstances.*
import kotlin.test.assertEquals
import kotlin.test.assertTrue


class ApplicationSecurityConfigTest : AbstractTest() {

    @Test
    fun checkSecurityAnnotations_success() {
        getApplicationRunner().run {
            assertTrue(it.isRunning)
        }
    }

    @Test
    fun checkSecurityAnnotations_incorrectAnnotationClass() {
        val expectedMessage =
            "Annotation ${SecuredTelegramListener::class.java.name} used in not TelegramLongPollingBot implementations:\n" +
                    "invalidClass1 ${InvalidClass1::class.java.name}\n" +
                    "invalidClass2 ${InvalidClass2::class.java.name}"

        getApplicationRunner().withUserConfiguration(InvalidAuthAnnotationConfig::class.java)
            .run {
                assertNotNull(it.startupFailure)
                assertEquals(expectedMessage, it.startupFailure.message)
            }
    }

    private fun getApplicationRunner() = ApplicationContextRunner()
        .withBean(ValidClass::class.java)
        .withUserConfiguration(TestAuthConfig::class.java)
        .withUserConfiguration(ApplicationSecurityConfig::class.java)
        .withUserConfiguration(AuthenticationConfiguration::class.java)
}