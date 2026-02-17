package io.kamae.family.bot.purchases.service

import arrow.core.Either
import io.kamae.family.bot.core.api.MessageHistoryProvider
import io.kamae.family.bot.core.api.TelegramBotMessageSender
import io.kamae.family.bot.core.api.model.MessageHistoryCategory
import io.kamae.family.bot.core.domain.model.TelegramActionResult
import io.kamae.family.bot.core.service.AbstractActionService
import io.kamae.family.bot.purchases.client.PurchasesServiceClient
import io.kamae.family.bot.purchases.client.dto.GetProductsInfoRsDto
import io.kamae.family.bot.purchases.constants.PurchasesConstants
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessages

abstract class AbstractListProductsActionService(
    private val messageHistoryProvider: MessageHistoryProvider,
    private val purchasesServiceClient: PurchasesServiceClient,
    sender: TelegramBotMessageSender
) : AbstractActionService(sender) {

    protected fun getAndProcessProductList(
        onSuccess: (response: GetProductsInfoRsDto) -> TelegramActionResult
    ): TelegramActionResult {
        val products = Either.catch { purchasesServiceClient.getProductsInfo() }

        return products.fold(
            {
                error("Ошибка от внешнего сервиса: ${it.message}")
            }, {
                onSuccess(it)
            }
        )
    }

    protected fun findAndDeletePreviousMessages(chatId: Long) {
        val forDelete = findPreviousMessages(chatId)

        if (forDelete.isNotEmpty()) {
            deletePreviousMessages(chatId, forDelete)
        }
    }

    private fun deletePreviousMessages(chatId: Long, forDelete: List<Int>) {
        val delete = DeleteMessages(chatId.toString(), forDelete)
        runCatching { sender.sendMessage(delete) }
        messageHistoryProvider.removeHistory(chatId, MessageHistoryCategory(PurchasesConstants.PRODUCT_LIST_CATEGORY))
    }

    private fun findPreviousMessages(chatId: Long) = messageHistoryProvider.getHistory(
        chatId,
        MessageHistoryCategory(PurchasesConstants.PRODUCT_LIST_CATEGORY)
    ).map { it.messageId }
}