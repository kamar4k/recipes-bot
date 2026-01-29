package io.kamae.family.bot.listener.delegate

import io.kamae.family.bot.domain.telegram.dto.TelegramAction
import io.kamae.family.bot.domain.telegram.dto.TelegramResponse
import io.kamae.family.bot.domain.telegram.dto.TelegramUserInfo
import io.kamae.family.bot.domain.telegram.parser.TelegramRecipesMessageHandler
import io.kamae.family.bot.security.AuthorizationUtils
import io.kamae.family.bot.security.annotation.SecuredTelegramListener
import io.kamae.family.bot.service.factory.ActionServiceFactory
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
    private val authorizationUtils: AuthorizationUtils
): TelegramBotDelegate {
    override fun processUpdate(update: Update): TelegramResponse {
        val (chatId: Long, text: String) = getChatIdAndTextFromUpdate(update)

        return try {
            val parsedResult = telegramMessageHandler.parseTelegramMessage(text, chatId)

            parsedResult.fold(
                {
                    it
                }, {
                    val userName = authorizationUtils.getUserName()
                    val action = TelegramAction(it, TelegramUserInfo(chatId, userName))
                    telegramBotHandlerFactory.getActionService(it.command).executeAndGetResponse(action)
                }
            )
        } catch (ex: AuthorizationDeniedException) {
            TelegramResponse("У вас не хватает прав на выполнение команды", chatId)
        }
        catch (ex: Exception) {
            TelegramResponse(ex.message ?: "Ошибка обработки запроса", chatId)
        }
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