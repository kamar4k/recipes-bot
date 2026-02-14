package io.kamae.family.bot.core.security.aspect

import io.kamae.family.bot.AbstractTest
import io.kamae.family.bot.core.domain.model.TelegramUpdateEvent
import io.kamae.family.bot.core.security.consts.AuthorizationConstants.EMPTY_PASSWORD
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.aspectj.lang.JoinPoint
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.telegram.telegrambots.meta.api.objects.*
import org.telegram.telegrambots.meta.api.objects.inlinequery.ChosenInlineQuery
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery

class SecuredTelegramListenerAspectTest : AbstractTest() {

    companion object {
        private const val USERNAME = "user1"
    }

    @MockK
    private lateinit var authenticationManager: AuthenticationManager

    @InjectMockKs
    private lateinit var securedTelegramListenerAspect: SecuredTelegramListenerAspect

    private val mockedJoinPoint: JoinPoint = mockk<JoinPoint>()
    private val mockedUpdate: Update = mockk<Update>(relaxed = true)

    private val message: Message = mockk<Message>()
    private val callbackQuery: CallbackQuery = mockk<CallbackQuery>()
    private val inlineQuery: InlineQuery = mockk<InlineQuery>()
    private val chosenInlineQuery: ChosenInlineQuery = mockk<ChosenInlineQuery>()
    private val chat: Chat = mockk<Chat>()

    private val user: User = mockk<User>()

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)

        every { user.userName } returns USERNAME

        every { authenticationManager.authenticate(any<Authentication>()) } returns mockk<Authentication>()
    }

    @ParameterizedTest(name = "{index}. {0}")
    @MethodSource("testCases")
    fun fillAuthorizationContext_success(desc: String, mockAction: () -> Unit, resultUsername: String?) {
        mockAction.invoke()

        securedTelegramListenerAspect.fillAuthorizationContext(mockedJoinPoint, TelegramUpdateEvent(mockedUpdate))

        verify { authenticationManager.authenticate(expectedAuthentication(resultUsername)) }
    }

    private fun testCases(): List<Arguments> = listOf(
        Arguments.of("from message", { mockUpdateForMessage() }, USERNAME),
        Arguments.of("from callbackQuery", { mockUpdateForCallbackQuery() }, USERNAME),
        Arguments.of("from inlineQuery", { mockUpdateForInlineQuery() }, USERNAME),
        Arguments.of("from chosenInlineQuery", { mockUpdateForChosenInlineQuery() }, USERNAME),
        Arguments.of("from channelPost", { mockUpdateForChannelPost() }, USERNAME),
        Arguments.of("from editedChannelPost", { mockUpdateForEditedChannelPost() }, USERNAME),
        Arguments.of("from editedMessage", { mockUpdateForEditedMessage() }, USERNAME),
        Arguments.of("update has not data", { }, null),
    )

    private fun mockUpdateForMessage() {
        every { mockedUpdate.hasMessage() } returns true
        every { mockedUpdate.message } returns message
        every { message.from } returns user
    }

    private fun mockUpdateForCallbackQuery() {
        every { mockedUpdate.hasCallbackQuery() } returns true
        every { mockedUpdate.callbackQuery } returns callbackQuery
        every { callbackQuery.from } returns user
    }

    private fun mockUpdateForInlineQuery() {
        every { mockedUpdate.hasInlineQuery() } returns true
        every { mockedUpdate.inlineQuery } returns inlineQuery
        every { inlineQuery.from } returns user
    }

    private fun mockUpdateForChosenInlineQuery() {
        every { mockedUpdate.hasChosenInlineQuery() } returns true
        every { mockedUpdate.chosenInlineQuery } returns chosenInlineQuery
        every { chosenInlineQuery.from } returns user
    }

    private fun mockUpdateForChannelPost() {
        every { mockedUpdate.hasChannelPost() } returns true
        every { mockedUpdate.channelPost } returns message
        every { message.chat } returns chat
        every { chat.userName } returns USERNAME
    }

    private fun mockUpdateForEditedChannelPost() {
        every { mockedUpdate.hasEditedChannelPost() } returns true
        every { mockedUpdate.editedChannelPost } returns message
        every { message.chat } returns chat
        every { chat.userName } returns USERNAME
    }

    private fun mockUpdateForEditedMessage() {
        every { mockedUpdate.editedMessage } returns message
        every { mockedUpdate.hasEditedMessage() } returns true
        every { message.from } returns user

    }

    private fun expectedAuthentication(username: String?) =
        UsernamePasswordAuthenticationToken(username, EMPTY_PASSWORD)
}