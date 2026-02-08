package io.kamae.family.bot.core.testinstances.auth

import arrow.core.Either
import io.kamae.family.bot.core.domain.model.TelegramUpdateEvent
import io.kamae.family.bot.core.listener.delegate.TelegramBotUpdateHandler
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
                RecipesUserRole.ROLE_RECIPES_GUEST to guestTestInstance,
                RecipesUserRole.ROLE_RECIPES_READER to readerTestInstance,
                RecipesUserRole.ROLE_RECIPES_EDITOR to editorTestInstance,
                RecipesUserRole.ROLE_RECIPES_ADMIN to adminTestInstance,
            )
        )
}

@SecuredTelegramListener
open class TestSecuredTelegramBotDelegate(
    private val securedClassesMap: Map<RecipesUserRole, AbstractTestAuthorizationInstance>
) : TelegramBotUpdateHandler {
    override fun processUpdate(telegramUpdateEvent: TelegramUpdateEvent) {
        val results: MutableMap<RecipesUserRole, Either<Throwable, Boolean>> = mutableMapOf()

        results[RecipesUserRole.ROLE_RECIPES_GUEST] = Either.catch { securedClassesMap[RecipesUserRole.ROLE_RECIPES_GUEST]!!.returnTrue() }
        results[RecipesUserRole.ROLE_RECIPES_READER] = Either.catch { securedClassesMap[RecipesUserRole.ROLE_RECIPES_READER]!!.returnTrue() }
        results[RecipesUserRole.ROLE_RECIPES_EDITOR] = Either.catch { securedClassesMap[RecipesUserRole.ROLE_RECIPES_EDITOR]!!.returnTrue() }
        results[RecipesUserRole.ROLE_RECIPES_ADMIN] = Either.catch { securedClassesMap[RecipesUserRole.ROLE_RECIPES_ADMIN]!!.returnTrue() }

        fixResult(results)
    }

    open fun fixResult(result: MutableMap<RecipesUserRole, Either<Throwable, Boolean>>) {
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

@PreAuthorize("hasRole('RECIPES_GUEST')")
@TestComponent
class GuestTestInstance : AbstractTestAuthorizationInstance {
    override fun returnTrue() = true
}
