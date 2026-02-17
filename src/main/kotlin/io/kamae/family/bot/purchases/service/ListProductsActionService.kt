package io.kamae.family.bot.purchases.service

import io.kamae.family.bot.core.api.MessageHistoryProvider
import io.kamae.family.bot.core.api.TelegramBotMessageSender
import io.kamae.family.bot.core.domain.model.TelegramAction
import io.kamae.family.bot.core.domain.model.TelegramActionResult
import io.kamae.family.bot.core.domain.model.TelegramResponse
import io.kamae.family.bot.purchases.api.ListProductsSender
import io.kamae.family.bot.purchases.client.PurchasesServiceClient
import io.kamae.family.bot.purchases.client.dto.GetProductsInfoRsDto
import io.kamae.family.bot.purchases.client.dto.ProductInfoDto
import io.kamae.family.bot.recipes.domain.keyboard.BaseKeyboard
import org.springframework.stereotype.Service
import java.time.format.DateTimeFormatter

@Service
class ListProductsActionService(
    messageHistoryProvider: MessageHistoryProvider,
    purchasesServiceClient: PurchasesServiceClient,
    sender: TelegramBotMessageSender
) : AbstractListProductsActionService(messageHistoryProvider, purchasesServiceClient, sender), ListProductsSender {

    override fun executeAction(telegramAction: TelegramAction): TelegramActionResult {
        return getAndPushProductsList(telegramAction.telegramUserInfo.chatId)
    }

    override fun getAndPushProductsList(chatId: Long): TelegramActionResult {
        val result = getAndProcessProductList {
            val productsStr = getProductsStatisticStr(it)
            TelegramActionResult(
                TelegramResponse(
                    "Продукт\t|\tКоличество\t|\tПрогноз\n" +
                            productsStr,
                    chatId,
                    BaseKeyboard
                )
            )
        }

        findAndDeletePreviousMessages(chatId)
        sendResult(result)

        return result
    }

    private fun getProductsStatisticStr(productsRs: GetProductsInfoRsDto): String {
        return productsRs.data.joinToString(separator = "\n") {
            "${it.name}\t|\tКол-во: ${it.quantity} \t|\tПрогноз: ${getPredication(it)}"
        }
    }

    private fun getPredication(it: ProductInfoDto) =
        it.predication?.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")) ?: "?"
}