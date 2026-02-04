package io.kamae.family.bot.core.security

import arrow.core.Either
import com.ninjasquad.springmockk.SpykBean
import io.kamae.family.bot.AbstractTest
import io.kamae.family.bot.FamilyBotApplication
import io.kamae.family.bot.core.jpa.entity.ApplicationUserEntity
import io.kamae.family.bot.core.jpa.repository.ApplicationUserRepository
import io.kamae.family.bot.core.testinstances.auth.ApplicationAuthorizationTestConfiguration
import io.kamae.family.bot.core.testinstances.auth.TestSecuredTelegramBotDelegate
import io.mockk.CapturingSlot
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.security.authorization.AuthorizationDeniedException
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.User
import java.util.*
import kotlin.reflect.KClass

@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [FamilyBotApplication::class, ApplicationAuthorizationTestConfiguration::class],
)

class AuthorizationTest : AbstractTest() {

    companion object {
        private const val TEST_USER_NAME = "testuser"
    }

    @Autowired
    private lateinit var applicationUserRepository: ApplicationUserRepository

    @SpykBean
    private lateinit var testSecuredTelegramBot: TestSecuredTelegramBotDelegate

    private val update = mockk<Update>()
    private val message = mockk<Message>()
    private val user = mockk<User>()

    @BeforeEach
    fun afterEach() {
        applicationUserRepository.deleteAll()
    }

    @ParameterizedTest
    @MethodSource("testCases")
    fun testAuthorization(role: UserRole?, expected: Map<UserRole, Either<KClass<Throwable>, Boolean>>) {

        val slot = CapturingSlot<MutableMap<UserRole, Either<Throwable, Boolean>>>()
        justRun { testSecuredTelegramBot.fixResult(capture(slot)) }

        every { update.hasMessage() } returns true
        every { update.message } returns message
        every { message.from } returns user
        every { user.userName } returns TEST_USER_NAME

        role?.let {
            val userEntity = ApplicationUserEntity(UUID.randomUUID(), TEST_USER_NAME, role)
            applicationUserRepository.save(userEntity)
        }

        testSecuredTelegramBot.processUpdate(update)

        slot.captured.forEach {
            val expectedElement = expected[it.key]!!

            if (expectedElement.isLeft()) {
                assertTrue(it.value.isLeft())
                assertEquals(expectedElement.leftOrNull()?.java, it.value.leftOrNull()?.javaClass)
            } else {
                assertEquals(expectedElement.getOrNull(), it.value.getOrNull())
            }
        }
    }

    private fun testCases(): List<Arguments> = listOf(
        Arguments.of(
            null,
            mapOf<UserRole, Either<KClass<out Throwable>, Boolean>>(
                UserRole.ROLE_GUEST to Either.Right(true),
                UserRole.ROLE_READER to Either.Left(AuthorizationDeniedException::class),
                UserRole.ROLE_EDITOR to Either.Left(AuthorizationDeniedException::class),
                UserRole.ROLE_ADMIN to Either.Left(AuthorizationDeniedException::class),
            )
        ),
        Arguments.of(
            UserRole.ROLE_GUEST,
            mapOf<UserRole, Either<KClass<out Throwable>, Boolean>>(
                UserRole.ROLE_GUEST to Either.Right(true),
                UserRole.ROLE_READER to Either.Left(AuthorizationDeniedException::class),
                UserRole.ROLE_EDITOR to Either.Left(AuthorizationDeniedException::class),
                UserRole.ROLE_ADMIN to Either.Left(AuthorizationDeniedException::class),
            )
        ),
        Arguments.of(
            UserRole.ROLE_READER,
            mapOf<UserRole, Either<KClass<out Throwable>, Boolean>>(
                UserRole.ROLE_GUEST to Either.Right(true),
                UserRole.ROLE_READER to Either.Right(true),
                UserRole.ROLE_EDITOR to Either.Left(AuthorizationDeniedException::class),
                UserRole.ROLE_ADMIN to Either.Left(AuthorizationDeniedException::class),
            )
        ),
        Arguments.of(
            UserRole.ROLE_EDITOR,
            mapOf<UserRole, Either<KClass<out Throwable>, Boolean>>(
                UserRole.ROLE_GUEST to Either.Right(true),
                UserRole.ROLE_READER to Either.Right(true),
                UserRole.ROLE_EDITOR to Either.Right(true),
                UserRole.ROLE_ADMIN to Either.Left(AuthorizationDeniedException::class),
            )
        ),
        Arguments.of(
            UserRole.ROLE_ADMIN,
            mapOf<UserRole, Either<KClass<out Throwable>, Boolean>>(
                UserRole.ROLE_GUEST to Either.Right(true),
                UserRole.ROLE_READER to Either.Right(true),
                UserRole.ROLE_EDITOR to Either.Right(true),
                UserRole.ROLE_ADMIN to Either.Right(true),
            )
        )
    )
}