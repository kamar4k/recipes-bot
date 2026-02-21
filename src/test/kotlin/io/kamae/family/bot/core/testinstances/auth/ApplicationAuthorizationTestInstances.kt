package io.kamae.family.bot.core.testinstances.auth

import arrow.core.Either
import io.kamae.family.bot.core.domain.model.TelegramUpdateEvent
import io.kamae.family.bot.core.listener.delegate.TelegramBotUpdateHandler
import io.kamae.family.bot.core.security.BotUserRole
import io.kamae.family.bot.core.security.annotation.SecuredTelegramListener
import io.kamae.family.bot.recipes.domain.RecipesUserRole
import org.springframework.boot.test.context.TestComponent
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Primary
import org.springframework.security.access.prepost.PreAuthorize


@TestConfiguration
@ComponentScan(basePackageClasses = [ApplicationAuthorizationTestConfiguration::class])
class ApplicationAuthorizationTestConfiguration {
    @Bean
    @Primary
    fun securedTelegramBotDelegate(
        guestTestInstance: GuestTestInstance,
        readerTestInstance: ReaderTestInstance,
        editorTestInstance: EditorTestInstance,
        adminTestInstance: AdminTestInstance,
    ): TestSecuredTelegramBotDelegate =
        TestSecuredTelegramBotDelegate(
            mapOf(
                BotUserRole.ROLE_GUEST.name to guestTestInstance,
                RecipesUserRole.ROLE_RECIPES_READER.name to readerTestInstance,
                RecipesUserRole.ROLE_RECIPES_EDITOR.name to editorTestInstance,
                RecipesUserRole.ROLE_RECIPES_ADMIN.name to adminTestInstance,
            )
        )
}

@SecuredTelegramListener
open class TestSecuredTelegramBotDelegate(
    private val securedClassesMap: Map<String, AbstractTestAuthorizationInstance>
) : TelegramBotUpdateHandler {
    override fun processUpdate(telegramUpdateEvent: TelegramUpdateEvent) {
        val results: MutableMap<String, Either<Throwable, Boolean>> = mutableMapOf()

        results[BotUserRole.ROLE_GUEST.name] = Either.catch { securedClassesMap[BotUserRole.ROLE_GUEST.name]!!.returnTrue() }
        results[RecipesUserRole.ROLE_RECIPES_READER.name] = Either.catch { securedClassesMap[RecipesUserRole.ROLE_RECIPES_READER.name]!!.returnTrue() }
        results[RecipesUserRole.ROLE_RECIPES_EDITOR.name] = Either.catch { securedClassesMap[RecipesUserRole.ROLE_RECIPES_EDITOR.name]!!.returnTrue() }
        results[RecipesUserRole.ROLE_RECIPES_ADMIN.name] = Either.catch { securedClassesMap[RecipesUserRole.ROLE_RECIPES_ADMIN.name]!!.returnTrue() }

        fixResult(results)
    }

    open fun fixResult(result: MutableMap<String, Either<Throwable, Boolean>>) {
    }

}

interface AbstractTestAuthorizationInstance {
    fun returnTrue(): Boolean
}

@PreAuthorize("hasRole('RECIPES_ADMIN')")
@TestComponent
class AdminTestInstance : AbstractTestAuthorizationInstance {
    override fun returnTrue() = true
}

@PreAuthorize("hasRole('RECIPES_EDITOR')")
@TestComponent
class EditorTestInstance : AbstractTestAuthorizationInstance {
    override fun returnTrue() = true
}

@PreAuthorize("hasRole('RECIPES_READER')")
@TestComponent
class ReaderTestInstance : AbstractTestAuthorizationInstance {
    override fun returnTrue() = true
}

@PreAuthorize("hasRole('GUEST')")
@TestComponent
class GuestTestInstance : AbstractTestAuthorizationInstance {
    override fun returnTrue() = true
}
