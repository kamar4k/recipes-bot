package io.kamae.family.bot.listener

import com.ninjasquad.springmockk.MockkBean
import com.ninjasquad.springmockk.SpykBean
import io.kamae.family.bot.AbstractIntegrationTest
import io.kamae.family.bot.core.domain.listener.RecipesBot
import io.kamae.family.bot.core.domain.model.CommandContext
import io.kamae.family.bot.core.domain.model.TelegramActionResult
import io.kamae.family.bot.core.domain.model.TelegramResponse
import io.kamae.family.bot.core.domain.parser.TelegramRecipesMessageHandler
import io.kamae.family.bot.core.provider.ContextProvider
import io.kamae.family.bot.core.security.AuthorizationUtils
import io.kamae.family.bot.core.security.aspect.SecuredTelegramListenerAspect
import io.kamae.family.bot.core.api.ActionService
import io.kamae.family.bot.core.factory.ActionServiceFactory
import io.kamae.family.bot.core.exception.TelegramException
import io.mockk.every
import io.mockk.justRun
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.security.authorization.AuthorizationDeniedException
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.CallbackQuery
import org.telegram.telegrambots.meta.api.objects.MaybeInaccessibleMessage
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update

class RecipesBotTest : AbstractIntegrationTest() {

    @SpykBean
    private lateinit var recipesBot: RecipesBot

    @MockkBean
    private lateinit var telegramMessageHandler: TelegramRecipesMessageHandler

    @MockkBean
    private lateinit var telegramBotHandlerFactory: ActionServiceFactory

    @MockkBean
    private lateinit var securedTelegramListenerAspect: SecuredTelegramListenerAspect

    @MockkBean
    private lateinit var authorizationUtils: AuthorizationUtils

    @MockkBean
    private lateinit var contextProvider: ContextProvider

    private val actionService: ActionService = mockk<ActionService>()

    private val update: Update = mockk<Update>()
    private val message: Message = mockk<Message>()
    private val callbackQuery: CallbackQuery = mockk<CallbackQuery>()
    private val callbackMessage: MaybeInaccessibleMessage = mockk<MaybeInaccessibleMessage>()

    @BeforeEach
    fun mockkHandler() {
        justRun { securedTelegramListenerAspect.fillAuthorizationContext(any(), any()) }
        justRun { contextProvider.appendAnswer(TEST_CHAT_ID, any()) }
        justRun { contextProvider.createContext(TEST_CHAT_ID, any()) }
        justRun { contextProvider.removeContextForChatId(TEST_CHAT_ID) }
        justRun { contextProvider.setNextQuestionForChatId(TEST_CHAT_ID, any()) }

        every { telegramBotHandlerFactory.getActionService(TELEGRAM_COMMAND_TEXT) } returns actionService
        every { update.message } returns message
        every { callbackQuery.message } returns callbackMessage
        every { update.callbackQuery } returns callbackQuery
        every { authorizationUtils.getUserName() } returns TEST_AUTHOR
    }

    @Test
    fun getBotUsername_success() {
        val result = recipesBot.botUsername

        assertEquals("family-bot", result)
    }

    @Test
    fun onUpdateReceived_successMessage() {
        hasMessageReturnsTrue()
        messageReturnsChatId()
        messsageReturnsText()
        every {
            telegramMessageHandler.parseMessageAndGetContext(
                TELEGRAM_MESSAGE_TEXT,
                TEST_CHAT_ID
            )
        } returns CommandContext(
            TELEGRAM_COMMAND_TEXT, null
        )
        every { actionService.executeAndGetResult(any()) } returns TelegramActionResult(
            TelegramResponse(
                TELEGRAM_RESPONSE_TEXT, TEST_CHAT_ID
            )
        )
        every { contextProvider.hasContext(TEST_CHAT_ID) } returns false

        every { recipesBot.execute(any<SendMessage>()) } returns null

        recipesBot.onUpdateReceived(update)

        verify {
            recipesBot.execute(SendMessage(TEST_CHAT_ID.toString(), TELEGRAM_RESPONSE_TEXT))
        }

        verify {
            contextProvider.createContext(TEST_CHAT_ID, CommandContext(TELEGRAM_COMMAND_TEXT, null))
        }
        verify {
            contextProvider.removeContextForChatId(TEST_CHAT_ID)
        }

        verifyAuthRunning()
    }

    @Test
    fun onUpdateReceived_successMessageWithExistingContext() {
        hasMessageReturnsTrue()
        messageReturnsChatId()
        messsageReturnsText()
        every { actionService.executeAndGetResult(any()) } returns TelegramActionResult(
            TelegramResponse(
                TELEGRAM_RESPONSE_TEXT, TEST_CHAT_ID
            ),
            CommandContext.Question(TELEGRAM_COMMAND_QUESTION)
        )
        every { contextProvider.hasContext(TEST_CHAT_ID) } returns true
        every { contextProvider.getContextForChatId(TEST_CHAT_ID) } returns CommandContext(
            TELEGRAM_COMMAND_TEXT, null
        )

        every { recipesBot.execute(any<SendMessage>()) } returns null

        recipesBot.onUpdateReceived(update)

        verify {
            recipesBot.execute(SendMessage(TEST_CHAT_ID.toString(), TELEGRAM_RESPONSE_TEXT))
        }

        verify { contextProvider.appendAnswer(TEST_CHAT_ID, CommandContext.Answer(TELEGRAM_MESSAGE_TEXT)) }
        verify { contextProvider.getContextForChatId(TEST_CHAT_ID) }
        verify {
            contextProvider.setNextQuestionForChatId(
                TEST_CHAT_ID,
                CommandContext.Question(TELEGRAM_COMMAND_QUESTION)
            )
        }
        verify(exactly = 0) {
            contextProvider.removeContextForChatId(TEST_CHAT_ID)
        }

        verifyAuthRunning()
    }

    @Test
    fun onUpdateReceived_successCallback() {
        every { update.hasMessage() } returns false
        every { update.hasCallbackQuery() } returns true
        every { callbackMessage.chatId } returns TEST_CHAT_ID
        every { callbackQuery.data } returns TELEGRAM_MESSAGE_TEXT
        every {
            telegramMessageHandler.parseMessageAndGetContext(
                TELEGRAM_MESSAGE_TEXT,
                TEST_CHAT_ID
            )
        } returns CommandContext(
            TELEGRAM_COMMAND_TEXT, null
        )
        every { contextProvider.hasContext(TEST_CHAT_ID) } returns false

        every { actionService.executeAndGetResult(any()) } returns TelegramActionResult(
            TelegramResponse(
                TELEGRAM_RESPONSE_TEXT, TEST_CHAT_ID
            )
        )

        every { recipesBot.execute(any<SendMessage>()) } returns null

        recipesBot.onUpdateReceived(update)

        verify {
            recipesBot.execute(SendMessage(TEST_CHAT_ID.toString(), TELEGRAM_RESPONSE_TEXT))
        }

        verify {
            contextProvider.createContext(TEST_CHAT_ID, CommandContext(TELEGRAM_COMMAND_TEXT, null))
        }
        verify {
            contextProvider.removeContextForChatId(TEST_CHAT_ID)
        }

        verifyAuthRunning()
    }


    @ParameterizedTest
    @MethodSource("errorTestCases")
    fun onUpdateReceived_commonFail(errorMessage: String?, expectedResponse: String) {
        hasMessageReturnsTrue()
        messageReturnsChatId()
        messsageReturnsText()
        every {
            telegramMessageHandler.parseMessageAndGetContext(
                TELEGRAM_MESSAGE_TEXT,
                TEST_CHAT_ID
            )
        } returns CommandContext(TELEGRAM_COMMAND_TEXT, null)
        every { contextProvider.hasContext(TEST_CHAT_ID) } returns false

        every { actionService.executeAndGetResult(any()) } throws RuntimeException(errorMessage)

        every { recipesBot.execute(any<SendMessage>()) } returns null

        recipesBot.onUpdateReceived(update)

        verify {
            recipesBot.execute(SendMessage(TEST_CHAT_ID.toString(), expectedResponse))
        }
        verify {
            contextProvider.createContext(TEST_CHAT_ID, CommandContext(TELEGRAM_COMMAND_TEXT, null))
        }
        verify {
            contextProvider.removeContextForChatId(TEST_CHAT_ID)
        }

        verifyAuthRunning()
    }

    @Test
    fun onUpdateReceived_authFail() {
        hasMessageReturnsTrue()
        messageReturnsChatId()
        messsageReturnsText()
        every {
            telegramMessageHandler.parseMessageAndGetContext(
                TELEGRAM_MESSAGE_TEXT,
                TEST_CHAT_ID
            )
        } returns CommandContext(TELEGRAM_COMMAND_TEXT, null)
        every { contextProvider.hasContext(TEST_CHAT_ID) } returns false

        every { actionService.executeAndGetResult(any()) } throws AuthorizationDeniedException("")

        every { recipesBot.execute(any<SendMessage>()) } returns null

        recipesBot.onUpdateReceived(update)

        verify {
            contextProvider.createContext(TEST_CHAT_ID, CommandContext(TELEGRAM_COMMAND_TEXT, null))
        }
        verify {
            contextProvider.removeContextForChatId(TEST_CHAT_ID)
        }
        verify {
            recipesBot.execute(SendMessage(TEST_CHAT_ID.toString(), "У вас не хватает прав на выполнение команды"))
        }

        verifyAuthRunning()
    }

    @Test
    fun onUpdateReceived_chatIdNotFound() {
        every { update.hasMessage() } returns false
        every { update.hasCallbackQuery() } returns false

        val error = assertThrows<IllegalStateException> { recipesBot.onUpdateReceived(update) }

        assertEquals("Не удалось определить chatId и text в сообщении", error.message)

        verifyAuthRunning()
    }

    @Test
    fun onUpdateReceived_failParse() {
        hasMessageReturnsTrue()
        messageReturnsChatId()
        messsageReturnsText()
        every { contextProvider.hasContext(TEST_CHAT_ID) } returns false
        every {
            telegramMessageHandler.parseMessageAndGetContext(
                TELEGRAM_MESSAGE_TEXT,
                TEST_CHAT_ID
            )
        } throws TelegramException(
            TelegramResponse(
                TELEGRAM_RESPONSE_TEXT, TEST_CHAT_ID
            )
        )


        every { recipesBot.execute(any<SendMessage>()) } returns null

        recipesBot.onUpdateReceived(update)


        verify(exactly = 0) { telegramBotHandlerFactory.getActionService(any()) }
        verify {
            recipesBot.execute(SendMessage(TEST_CHAT_ID.toString(), TELEGRAM_RESPONSE_TEXT))
        }
        verify {
            contextProvider.removeContextForChatId(TEST_CHAT_ID)
        }
        verifyAuthRunning()
    }

    private fun messsageReturnsText() {
        every { message.text } returns TELEGRAM_MESSAGE_TEXT
    }

    private fun messageReturnsChatId() {
        every { message.chatId } returns TEST_CHAT_ID
    }

    private fun hasMessageReturnsTrue() {
        every { update.hasMessage() } returns true
    }

    private fun errorTestCases(): List<Arguments> = listOf(
        Arguments.of("error", "error"),
        Arguments.of(null, "Ошибка обработки запроса")
    )

    private fun verifyAuthRunning() {
        verify { securedTelegramListenerAspect.fillAuthorizationContext(any(), update) }
    }
}