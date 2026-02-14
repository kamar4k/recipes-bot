package io.kamae.family.bot.core.testinstances

import io.kamae.family.bot.AbstractTest
import io.kamae.family.bot.core.domain.model.TelegramResponse
import io.kamae.family.bot.core.domain.model.TelegramUpdateEvent
import io.kamae.family.bot.core.jpa.repository.ApplicationUserRepository
import io.kamae.family.bot.core.listener.delegate.TelegramBotUpdateHandler
import io.kamae.family.bot.core.security.annotation.SecuredTelegramListener
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
open class ValidClass: TelegramBotUpdateHandler {
    init {
        logInitMessage(this.javaClass)
    }

    override fun processUpdate(telegramBotUpdateEvent: TelegramUpdateEvent) {

    }

}

fun logInitMessage(clazz: Class<out Any>) {
    LoggerFactory.getLogger(clazz).info("${clazz.name} initialized")
}