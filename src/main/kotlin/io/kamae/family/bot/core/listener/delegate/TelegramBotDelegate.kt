package io.kamae.family.bot.core.listener.delegate

import io.kamae.family.bot.core.api.ContextProvider
import io.kamae.family.bot.core.domain.model.*
import io.kamae.family.bot.core.domain.parser.TelegramRecipesMessageHandler
import io.kamae.family.bot.core.exception.TelegramException
import io.kamae.family.bot.core.factory.ActionServiceFactory
import io.kamae.family.bot.core.security.AuthorizationUtils
import io.kamae.family.bot.core.security.annotation.SecuredTelegramListener
import org.springframework.security.authorization.AuthorizationDeniedException
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Update

interface TelegramBotDelegate {
    fun processUpdate(update: Update): TelegramResponse
}

@Component
@SecuredTelegramListener
class RecipesBotDelegate(
    private val telegramMessageHandler: TelegramRecipesMessageHandler,
    private val telegramBotHandlerFactory: ActionServiceFactory,
    private val authorizationUtils: AuthorizationUtils,
    private val contextProvider: ContextProvider
) : TelegramBotDelegate {
    override fun processUpdate(update: Update): TelegramResponse {
        val (chatId: Long, text: String) = getChatIdAndTextFromUpdate(update)

        return try {
            val context = getContext(chatId, text)

            val actionResult = executeActionAndGetResult(context, chatId)

            setNextQuestionOrClearContext(actionResult, chatId)

            actionResult.telegramResponse
        } catch (ex: TelegramException) {
            contextProvider.removeContextForChatId(chatId)
            ex.telegramResponse
        } catch (ex: AuthorizationDeniedException) {
            contextProvider.removeContextForChatId(chatId)
            TelegramResponse("У вас не хватает прав на выполнение команды", chatId)
        } catch (ex: Exception) {
            contextProvider.removeContextForChatId(chatId)
            TelegramResponse(ex.message ?: "Ошибка обработки запроса", chatId)
        }
    }

    private fun setNextQuestionOrClearContext(actionResult: TelegramActionResult, chatId: Long) {
        (actionResult.nextQuestion?.let { contextProvider.setNextQuestionForChatId(chatId, it) }
            ?: also { contextProvider.removeContextForChatId(chatId) })
    }

    private fun executeActionAndGetResult(
        context: CommandContext,
        chatId: Long
    ): TelegramActionResult {
        val userName = authorizationUtils.getUserName()
        val action = TelegramAction(context, TelegramUserInfo(chatId, userName))
        val actionResult = telegramBotHandlerFactory.getActionService(context.command).executeAndGetResult(action)
        return actionResult
    }

    private fun getContext(
        chatId: Long,
        text: String
    ) = if (contextProvider.hasContext(chatId)) {
        appendAnswerAndGetContext(chatId, text)
    } else {
        createAndGetContext(text, chatId)
    }

    private fun createAndGetContext(
        text: String,
        chatId: Long
    ): CommandContext {
        val ctxForSave = telegramMessageHandler.parseMessageAndGetContext(text, chatId)
        contextProvider.createContext(chatId, ctxForSave)
        return ctxForSave
    }

    private fun appendAnswerAndGetContext(chatId: Long, answer: String): CommandContext {
        contextProvider.appendAnswer(chatId, CommandContext.Answer(answer))
        return contextProvider.getContextForChatId(chatId)!!
    }

    private fun getChatIdAndTextFromUpdate(update: Update): Pair<Long, String> {
        return if (update.hasMessage()) {
            update.message.chatId to update.message.text
        } else if (update.hasCallbackQuery()) {
            update.callbackQuery.message.chatId to update.callbackQuery.data
        } else {
            error("Не удалось определить chatId и text в сообщении")
        }
    }
}