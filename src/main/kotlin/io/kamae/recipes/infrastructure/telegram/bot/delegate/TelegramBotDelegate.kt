package io.kamae.recipes.infrastructure.telegram.bot.delegate

import io.kamae.recipes.infrastructure.security.annotation.SecuredTelegramListener
import io.kamae.recipes.infrastructure.telegram.dto.TelegramResponse
import io.kamae.recipes.infrastructure.telegram.handler.factory.TelegramBotHandlerFactory
import io.kamae.recipes.infrastructure.telegram.parser.TelegramMessageHandler
import org.springframework.security.authorization.AuthorizationDeniedException
import org.springframework.stereotype.Component
import org.telegram.telegrambots.meta.api.objects.Update

interface TelegramBotDelegate {
    fun processUpdate(update: Update): TelegramResponse
}

@Component
@SecuredTelegramListener
class RecipesBotDelegate(
    private val telegramMessageHandler: TelegramMessageHandler,
    private val telegramBotHandlerFactory: TelegramBotHandlerFactory
): TelegramBotDelegate {
    override fun processUpdate(update: Update): TelegramResponse {
        val (chatId: Long, text: String) = getChatIdAndTextFromUpdate(update)

        return try {
            val parsedResult = telegramMessageHandler.parseTelegramMessage(text, chatId)

            parsedResult.fold(
                {
                    it
                }, {
                    telegramBotHandlerFactory.getHandler(it.command).executeCommand(it.text, chatId)
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