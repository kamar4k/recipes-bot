package testinstances

import io.kamae.recipes.AbstractTest
import io.kamae.recipes.infrastructure.security.annotation.SecuredTelegramListener
import io.kamae.recipes.infrastructure.store.repository.ApplicationUserRepository
import io.mockk.mockk
import org.slf4j.LoggerFactory
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.telegram.telegrambots.bots.TelegramLongPollingBot
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
class InvalidClass1 {
    init {
        logInitMessage(this.javaClass)
    }

    @Suppress("unused")
    fun onUpdateReceived(@Suppress("UNUSED_PARAMETER") update: Update?) {
    }
}

@SecuredTelegramListener
class InvalidClass2 {
    init {
        logInitMessage(this.javaClass)
    }
    @Suppress("unused")
    fun someMethod() {
    }

}

@SecuredTelegramListener
class ValidClass: TelegramLongPollingBot("") {
    init {
        logInitMessage(this.javaClass)
    }

    override fun getBotUsername(): String {
        return "234"
    }

    override fun onUpdateReceived(p0: Update?) {

    }
}

fun logInitMessage(clazz: Class<out Any>) {
    LoggerFactory.getLogger(clazz).info("${clazz.name} initialized")
}