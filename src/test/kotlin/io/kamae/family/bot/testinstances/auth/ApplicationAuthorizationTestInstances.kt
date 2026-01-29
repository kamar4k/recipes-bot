package io.kamae.family.bot.testinstances.auth

import arrow.core.Either
import io.kamae.family.bot.domain.telegram.dto.TelegramResponse
import io.kamae.family.bot.listener.delegate.TelegramBotDelegate
import io.kamae.family.bot.security.UserRole
import io.kamae.family.bot.security.annotation.SecuredTelegramListener
import org.springframework.boot.test.context.TestComponent
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Primary
import org.springframework.security.access.prepost.PreAuthorize
import org.telegram.telegrambots.meta.api.objects.Update


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
                UserRole.ROLE_GUEST to guestTestInstance,
                UserRole.ROLE_READER to readerTestInstance,
                UserRole.ROLE_EDITOR to editorTestInstance,
                UserRole.ROLE_ADMIN to adminTestInstance,
            )
        )
}

@SecuredTelegramListener
open class TestSecuredTelegramBotDelegate(
    private val securedClassesMap: Map<UserRole, AbstractTestAuthorizationInstance>
) : TelegramBotDelegate {
    override fun processUpdate(update: Update): TelegramResponse {
        val results: MutableMap<UserRole, Either<Throwable, Boolean>> = mutableMapOf()

        results[UserRole.ROLE_GUEST] = Either.catch { securedClassesMap[UserRole.ROLE_GUEST]!!.returnTrue() }
        results[UserRole.ROLE_READER] = Either.catch { securedClassesMap[UserRole.ROLE_READER]!!.returnTrue() }
        results[UserRole.ROLE_EDITOR] = Either.catch { securedClassesMap[UserRole.ROLE_EDITOR]!!.returnTrue() }
        results[UserRole.ROLE_ADMIN] = Either.catch { securedClassesMap[UserRole.ROLE_ADMIN]!!.returnTrue() }

        fixResult(results)

        return TelegramResponse("", 1234L)
    }

    open fun fixResult(result: MutableMap<UserRole, Either<Throwable, Boolean>>) {
    }

}

interface AbstractTestAuthorizationInstance {
    fun returnTrue(): Boolean
}

@PreAuthorize("hasRole('ADMIN')")
@TestComponent
class AdminTestInstance : AbstractTestAuthorizationInstance {
    override fun returnTrue() = true
}

@PreAuthorize("hasRole('EDITOR')")
@TestComponent
class EditorTestInstance : AbstractTestAuthorizationInstance {
    override fun returnTrue() = true
}

@PreAuthorize("hasRole('READER')")
@TestComponent
class ReaderTestInstance : AbstractTestAuthorizationInstance {
    override fun returnTrue() = true
}

@PreAuthorize("hasRole('GUEST')")
@TestComponent
class GuestTestInstance : AbstractTestAuthorizationInstance {
    override fun returnTrue() = true
}
