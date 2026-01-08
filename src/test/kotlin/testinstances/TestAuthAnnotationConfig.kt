package testinstances

import io.kamae.recipes.AbstractTest
import io.kamae.recipes.infrastructure.security.annotation.SecuredTelegramListener
import io.kamae.recipes.infrastructure.store.repository.ApplicationUserRepository
import io.kamae.recipes.infrastructure.telegram.bot.delegate.TelegramBotDelegate
import io.kamae.recipes.infrastructure.telegram.dto.TelegramResponse
import io.mockk.mockk
import org.slf4j.LoggerFactory
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.telegram.telegrambots.meta.api.objects.Update

@TestConfiguration
class InvalidAuthAnnotationConfig : AbstractTest() {

    @Bean
    fun invalidClass1() = InvalidClass1()

    @Bean
    fun invalidClass2() = InvalidClass2()
}

@TestConfiguration
class TestAuthConfig {
    @Bean
    fun applicationUserRepository(): ApplicationUserRepository = mockk<ApplicationUserRepository>()
}

@SecuredTelegramListener
open class InvalidClass1 {
    init {
        logInitMessage(this.javaClass)
    }

    @Suppress("unused")
    fun processUpdate(@Suppress("UNUSED_PARAMETER") update: Update?): TelegramResponse {
        return TelegramResponse("", 1234L)
    }
}

@SecuredTelegramListener
open class InvalidClass2 {
    init {
        logInitMessage(this.javaClass)
    }
    @Suppress("unused")
    fun someMethod() {
    }

}

@SecuredTelegramListener
open class ValidClass: TelegramBotDelegate {
    init {
        logInitMessage(this.javaClass)
    }

    override fun processUpdate(update: Update): TelegramResponse {
        return TelegramResponse("", 1234L)
    }

}

fun logInitMessage(clazz: Class<out Any>) {
    LoggerFactory.getLogger(clazz).info("${clazz.name} initialized")
}