package io.kamae.family.bot.purchases.service

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
import io.kamae.family.bot.purchases.client.PurchasesServiceClient
import io.kamae.family.bot.purchases.client.dto.GetProductsInfoRsDto
import io.kamae.family.bot.purchases.constants.PurchasesConstants.PRODUCT_LIST_CATEGORY
import org.springframework.stereotype.Service

@Service
class ListProductsChangesActionService(
    private val messageHistoryProvider: MessageHistoryProvider,
    purchasesServiceClient: PurchasesServiceClient,
    sender: TelegramBotMessageSender
) : AbstractListProductsActionService(messageHistoryProvider, purchasesServiceClient, sender) {
    companion object {
        private val PLUS_EMOJI = EmojiParser.parseToUnicode(":heavy_plus_sign:")
        private val MINUS_EMOJI = EmojiParser.parseToUnicode(":heavy_minus_sign:")
    }

    override fun executeAction(telegramAction: TelegramAction): TelegramActionResult {
        val result = getAndProcessProductList {
            val buttons = getProductsWithButtons(it)
            TelegramActionResult(
                TelegramResponse(
                    "Продукт\t|\tКоличество\t|\tДействие",
                    telegramAction.getChatId(),
                    OneColumnKeyboard(buttons)
                )
            )
        }

        findAndDeletePreviousMessages(telegramAction.getChatId())
        sendResultAndSaveHistory(result, telegramAction.getChatId())

        return result
    }

    private fun getProductsWithButtons(productsRs: GetProductsInfoRsDto): List<TelegramButton> {
        val buttons = productsRs.data.map {
            val productText = "${it.name}\t|\tКол-во: ${it.quantity}\t|\t"

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

    private fun sendResultAndSaveHistory(result: TelegramActionResult, chatId: Long) {
        val messageId = sendResult(result)

        messageHistoryProvider.addToHistory(
            chatId,
            MessageHistoryCategory(PRODUCT_LIST_CATEGORY),
            MessageHistoryElement(messageId)
        )
    }
}