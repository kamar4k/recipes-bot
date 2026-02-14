package io.kamae.family.bot.core.listener.delegate

import io.kamae.family.bot.core.api.ContextProvider
import io.kamae.family.bot.core.api.TelegramBotMessageSender
import io.kamae.family.bot.core.domain.model.*
import io.kamae.family.bot.core.domain.parser.TelegramRecipesMessageHandler
import io.kamae.family.bot.core.exception.TelegramException
import io.kamae.family.bot.core.factory.ActionServiceFactory
import io.kamae.family.bot.core.security.AuthorizationUtils
import io.kamae.family.bot.core.security.annotation.SecuredTelegramListener
import io.kamae.family.bot.recipes.domain.keyboard.BaseKeyboard
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
        val params = getChatIdAndTextFromUpdate(telegramBotUpdateEvent.update)

        try {
            val context = getContext(params.chatId, params.text)

            val actionResult = executeActionAndGetResult(context, params.chatId, params.messageId)

            setNextQuestionOrClearContext(actionResult, params.chatId)
        } catch (ex: TelegramException) {
            contextProvider.removeContextForChatId(params.chatId)
            sendDefaultMessage(ex.telegramResponse)
        } catch (ex: AuthorizationDeniedException) {
            contextProvider.removeContextForChatId(params.chatId)
            sendDefaultMessage(TelegramResponse("У вас не хватает прав на выполнение команды", params.chatId))
        } catch (ex: Exception) {
            contextProvider.removeContextForChatId(params.chatId)
            sendDefaultMessage(TelegramResponse(ex.message ?: "Ошибка обработки запроса", params.chatId))
        }
    }

    private fun setNextQuestionOrClearContext(actionResult: TelegramActionResult, chatId: Long) {
        (actionResult.nextQuestion?.let { contextProvider.setNextQuestionForChatId(chatId, it) }
            ?: also { contextProvider.removeContextForChatId(chatId) })
    }

    private fun executeActionAndGetResult(
        context: CommandContext,
        chatId: Long,
        messageId: Int?
    ): TelegramActionResult {
        val userName = authorizationUtils.getUserName()
        val action = TelegramAction(context, TelegramUserInfo(chatId, userName), messageId)
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

    private fun getChatIdAndTextFromUpdate(update: Update): TelegramUpdateParams {
        return if (update.hasMessage()) {
            TelegramUpdateParams(update.message.chatId, update.message.text, update.message.messageId)
        } else if (update.hasCallbackQuery()) {
            TelegramUpdateParams(update.callbackQuery.message.chatId, update.callbackQuery.data, null)
        } else {
            error("Не удалось определить chatId и text в сообщении")
        }
    }

    private fun sendDefaultMessage(
        telegramResponse: TelegramResponse,
    ) {
        val sendMessage = SendMessage(telegramResponse.chatId.toString(), telegramResponse.text)
        sendMessage.replyMarkup = BaseKeyboard.getKeyboard()
        telegramBotMessageSender.sendMessage(sendMessage)
    }
}