package io.kamae.recipes.infrastructure.telegram.bot

import io.kamae.recipes.infrastructure.config.TelegramBotConfig
import io.kamae.recipes.infrastructure.security.annotation.SecuredTelegramListener
import io.kamae.recipes.infrastructure.telegram.dto.TelegramResponse
import io.kamae.recipes.infrastructure.telegram.handler.factory.TelegramBotHandlerFactory
import io.kamae.recipes.infrastructure.telegram.parser.TelegramMessageHandler
import org.springframework.security.authorization.AuthorizationDeniedException
import org.springframework.stereotype.Component
import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton

@Component
@SecuredTelegramListener
class RecipesBot(
    private val telegramBotConfig: TelegramBotConfig,
    private val telegramMessageHandler: TelegramMessageHandler,
    private val telegramBotHandlerFactory: TelegramBotHandlerFactory
) : TelegramLongPollingBot(telegramBotConfig.token) {
    override fun getBotUsername(): String = telegramBotConfig.name

    override fun onUpdateReceived(update: Update?) {
        if (update == null) return

        val (chatId: Long, text: String) = getChatIdAndTextFromUpdate(update)

        try {
            val parsedResult = telegramMessageHandler.parseTelegramMessage(text)

            parsedResult.fold(
                {
                    sendMessage(chatId, it)
                }, {
                    val response = telegramBotHandlerFactory.getHandler(it.command).executeCommand(it.text)
                    sendMessage(chatId, response)
                }
            )
        } catch (ex: AuthorizationDeniedException) {
            sendMessage(chatId, toResponse("У вас не хватает прав на выполнение команды"))
        }
        catch (ex: Exception) {
            sendMessage(chatId, toResponse(ex.message ?: "Ошибка обработки запроса"))
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

    private fun sendMessage(chatId: Long, response: TelegramResponse) {
        val sendMessage = SendMessage(chatId.toString(), response.text)

        val buttons = response.buttons?.map {
            val button = InlineKeyboardButton(it.name)
            button.callbackData = it.action
            listOf(button)
        }

        if (buttons != null) {
            sendMessage.replyMarkup = InlineKeyboardMarkup(buttons)
        }

        execute(sendMessage)
    }

    private fun toResponse(text: String): TelegramResponse = TelegramResponse(text)
}