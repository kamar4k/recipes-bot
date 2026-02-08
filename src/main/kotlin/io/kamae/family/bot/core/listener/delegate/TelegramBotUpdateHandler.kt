package io.kamae.family.bot.core.listener.delegate

import io.kamae.family.bot.core.api.ContextProvider
import io.kamae.family.bot.core.api.TelegramBotMessageSender
import io.kamae.family.bot.core.domain.model.*
import io.kamae.family.bot.core.domain.parser.TelegramRecipesMessageHandler
import io.kamae.family.bot.core.exception.TelegramException
import io.kamae.family.bot.core.factory.ActionServiceFactory
import io.kamae.family.bot.core.security.AuthorizationUtils
import io.kamae.family.bot.core.security.annotation.SecuredTelegramListener
import org.springframework.context.event.EventListener
import org.springframework.security.authorization.AuthorizationDeniedException
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update

interface TelegramBotUpdateHandler {
    fun processUpdate(telegramBotUpdateEvent: TelegramUpdateEvent)
}

@Component
@SecuredTelegramListener
class TelegramBotUpdateHandlerImpl(
    private val telegramMessageHandler: TelegramRecipesMessageHandler,
    private val telegramBotHandlerFactory: ActionServiceFactory,
    private val authorizationUtils: AuthorizationUtils,
    private val contextProvider: ContextProvider,
    private val telegramBotMessageSender: TelegramBotMessageSender
) : TelegramBotUpdateHandler {

    @EventListener(TelegramUpdateEvent::class)
    override fun processUpdate(telegramBotUpdateEvent: TelegramUpdateEvent) {
        val (chatId: Long, text: String) = getChatIdAndTextFromUpdate(telegramBotUpdateEvent.update)

        try {
            val context = getContext(chatId, text)

            val actionResult = executeActionAndGetResult(context, chatId)

            setNextQuestionOrClearContext(actionResult, chatId)
        } catch (ex: TelegramException) {
            contextProvider.removeContextForChatId(chatId)
            sendDefaultMessage(ex.telegramResponse)
        } catch (ex: AuthorizationDeniedException) {
            contextProvider.removeContextForChatId(chatId)
            sendDefaultMessage(TelegramResponse("У вас не хватает прав на выполнение команды", chatId))
        } catch (ex: Exception) {
            contextProvider.removeContextForChatId(chatId)
            sendDefaultMessage(TelegramResponse(ex.message ?: "Ошибка обработки запроса", chatId))
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
        return telegramBotHandlerFactory.getActionService(context.command)
            .executeAction(action)
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

    private fun sendDefaultMessage(
        telegramResponse: TelegramResponse,
    ) {
        val sendMessage = SendMessage(telegramResponse.chatId.toString(), telegramResponse.text)
        telegramBotMessageSender.sendMessage(sendMessage)
    }
}