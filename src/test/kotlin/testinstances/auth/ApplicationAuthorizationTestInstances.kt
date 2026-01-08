package testinstances.auth

import arrow.core.Either
import io.kamae.recipes.infrastructure.security.UserRole
import io.kamae.recipes.infrastructure.security.annotation.SecuredTelegramListener
import org.springframework.boot.test.context.TestComponent
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.security.access.prepost.PreAuthorize
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.objects.Update


@TestConfiguration
@ComponentScan(basePackageClasses = [ApplicationAuthorizationTestConfiguration::class])
class ApplicationAuthorizationTestConfiguration {
    @Bean
    fun securedTelegramBot(
        guestTestInstance: GuestTestInstance,
        readerTestInstance: ReaderTestInstance,
        editorTestInstance: EditorTestInstance,
        adminTestInstance: AdminTestInstance,
    ): TestSecuredTelegramBot =
        TestSecuredTelegramBot(
            mapOf(
                UserRole.ROLE_GUEST to guestTestInstance,
                UserRole.ROLE_READER to readerTestInstance,
                UserRole.ROLE_EDITOR to editorTestInstance,
                UserRole.ROLE_ADMIN to adminTestInstance,
            )
        )
}

@SecuredTelegramListener
open class TestSecuredTelegramBot(
    private val securedClassesMap: Map<UserRole, AbstractTestAuthorizationInstance>
) : TelegramLongPollingBot("TOKEN") {
    override fun getBotUsername(): String {
        return "USERNAME"
    }

    override fun onUpdateReceived(p0: Update?) {
        val results: MutableMap<UserRole, Either<Throwable, Boolean>> = mutableMapOf()

        results[UserRole.ROLE_GUEST] = Either.catch { securedClassesMap[UserRole.ROLE_GUEST]!!.returnTrue() }
        results[UserRole.ROLE_READER] = Either.catch { securedClassesMap[UserRole.ROLE_READER]!!.returnTrue() }
        results[UserRole.ROLE_EDITOR] = Either.catch { securedClassesMap[UserRole.ROLE_EDITOR]!!.returnTrue() }
        results[UserRole.ROLE_ADMIN] = Either.catch { securedClassesMap[UserRole.ROLE_ADMIN]!!.returnTrue() }

        fixResult(results)
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
