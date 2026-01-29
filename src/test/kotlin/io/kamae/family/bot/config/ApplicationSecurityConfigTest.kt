package io.kamae.family.bot.config

import io.kamae.family.bot.AbstractTest
import io.kamae.family.bot.security.annotation.SecuredTelegramListener
import io.kamae.family.bot.testinstances.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertNotNull
import org.springframework.boot.test.context.runner.ApplicationContextRunner
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
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
        val expectedMessageRegex =
            "Annotation ${SecuredTelegramListener::class.java.name} used in not TelegramBotDelegate implementations:\n" +
                    "invalidClass1 ${InvalidClass1::class.java.name}.*\n" +
                    "invalidClass2 ${InvalidClass2::class.java.name}.*$"

        getApplicationRunner().withUserConfiguration(InvalidAuthAnnotationConfig::class.java)
            .run {
                assertNotNull(it.startupFailure)
                assertTrue(it.startupFailure.message?.matches(Regex(expectedMessageRegex))?: false,
                    "expected pattern: $expectedMessageRegex\nactual:${it.startupFailure.message}")
            }
    }

    private fun getApplicationRunner() = ApplicationContextRunner()
        .withBean(ValidClass::class.java)
        .withUserConfiguration(TestAuthConfig::class.java)
        .withUserConfiguration(ApplicationSecurityConfig::class.java)
        .withUserConfiguration(AuthenticationConfiguration::class.java)
}