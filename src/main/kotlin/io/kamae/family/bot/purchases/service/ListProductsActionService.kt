package io.kamae.family.bot.purchases.service

import arrow.core.Either
import com.vdurmont.emoji.EmojiParser
import io.kamae.family.bot.common.domain.keyboard.OneColumnKeyboard
import io.kamae.family.bot.core.api.MessageHistoryProvider
import io.kamae.family.bot.core.api.TelegramBotMessageSender
import io.kamae.family.bot.core.api.model.MessageHistoryCategory
import io.kamae.family.bot.core.api.model.MessageHistoryElement
import io.kamae.family.bot.core.domain.model.TelegramAction
import io.kamae.family.bot.core.domain.model.TelegramActionResult
import io.kamae.family.bot.core.domain.model.TelegramButton
import io.kamae.family.bot.core.domain.model.TelegramResponse
import io.kamae.family.bot.core.service.AbstractActionService
import io.kamae.family.bot.purchases.api.ListProductsSender
import io.kamae.family.bot.purchases.client.PurchasesServiceClient
import io.kamae.family.bot.purchases.client.dto.GetProductsInfoRsDto
import io.kamae.family.bot.purchases.client.dto.ProductInfoDto
import io.kamae.family.bot.purchases.constants.PurchasesConstants.PRODUCT_LIST_CATEGORY
import org.springframework.stereotype.Service
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessages
import java.time.format.DateTimeFormatter

@Service
class ListProductsActionService(
    private val purchasesServiceClient: PurchasesServiceClient,
    private val messageHistoryProvider: MessageHistoryProvider,
    sender: TelegramBotMessageSender
) : AbstractActionService(sender), ListProductsSender {
    companion object {
        private val PLUS_EMOJI = EmojiParser.parseToUnicode(":heavy_plus_sign:")
        private val MINUS_EMOJI = EmojiParser.parseToUnicode(":heavy_minus_sign:")
    }

    override fun executeAction(telegramAction: TelegramAction): TelegramActionResult {
        return getAndPushProductsList(telegramAction.telegramUserInfo.chatId)
    }

    override fun getAndPushProductsList(chatId: Long): TelegramActionResult {
        val products = Either.catch { purchasesServiceClient.getProductsInfo() }

        val result = products.fold(
            {
                error("Ошибка от внешнего сервиса: ${it.message}")
            }, {
                val buttons = getProductsWithButtons(it)
                TelegramActionResult(
                    TelegramResponse(
                        "Продукт|Количество|Прогноз|Действие",
                        chatId,
                        OneColumnKeyboard(buttons)
                    )
                )
            }
        )

        findAndDeletePreviousMessages(chatId)
        sendResultAndSaveHistory(result, chatId)

        return result
    }

    private fun getProductsWithButtons(productsRs: GetProductsInfoRsDto): List<TelegramButton> {
        val buttons = productsRs.data.map {
            val productText = "${it.name} | Кол-во: ${it.quantity} | Прогноз: ${getPredication(it)} | "

            if (it.quantity > 0) {
                TelegramButton(productText + MINUS_EMOJI, "/reduce-product ${it.id} ${it.quantity}")
            } else {
                TelegramButton(productText + PLUS_EMOJI, "/increase-product ${it.id}")
            }
        }

        return buildList {
            addAll(buttons)
        }
    }

    private fun getPredication(it: ProductInfoDto) =
        it.predication?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) ?: "?"

    private fun findAndDeletePreviousMessages(chatId: Long) {
        val forDelete = findPreviousMessages(chatId)

        if (forDelete.isNotEmpty()) {
            deletePreviousMessages(chatId, forDelete)
        }
    }

    private fun deletePreviousMessages(chatId: Long, forDelete: List<Int>) {
        val delete = DeleteMessages(chatId.toString(), forDelete)
        sender.sendMessage(delete)
        messageHistoryProvider.removeHistory(chatId, MessageHistoryCategory(PRODUCT_LIST_CATEGORY))
    }

    private fun findPreviousMessages(chatId: Long) = messageHistoryProvider.getHistory(
        chatId,
        MessageHistoryCategory(PRODUCT_LIST_CATEGORY)
    ).map { it.messageId }

    private fun sendResultAndSaveHistory(result: TelegramActionResult, chatId: Long) {
        val messageId = sendResult(result)

        messageHistoryProvider.addToHistory(
            chatId,
            MessageHistoryCategory(PRODUCT_LIST_CATEGORY),
            MessageHistoryElement(messageId)
        )
    }
}