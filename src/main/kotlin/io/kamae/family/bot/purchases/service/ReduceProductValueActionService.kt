package io.kamae.family.bot.purchases.service

import arrow.core.Either
import io.kamae.family.bot.core.api.ActionService.Companion.prepareResultWithText
import io.kamae.family.bot.core.api.TelegramBotMessageSender
import io.kamae.family.bot.core.domain.model.TelegramAction
import io.kamae.family.bot.core.domain.model.TelegramActionResult
import io.kamae.family.bot.core.service.AbstractActionService
import io.kamae.family.bot.purchases.api.ListProductsSender
import io.kamae.family.bot.purchases.client.PurchasesServiceClient
import io.kamae.family.bot.purchases.client.dto.AddProductEventRqDto
import io.kamae.family.bot.purchases.client.dto.ChangeType
import io.kamae.family.bot.purchases.client.dto.ProductEventDto
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Service

@Service
@PreAuthorize("hasRole('PURCHASES_EDITOR')")
class ReduceProductValueActionService(
    private val purchasesServiceClient: PurchasesServiceClient,
    private val listProductsSender: ListProductsSender,
    sender: TelegramBotMessageSender
) : AbstractActionService(sender) {

    override fun executeAction(telegramAction: TelegramAction): TelegramActionResult {
        val text = telegramAction.commandContext.text!!

        val splitText = text.split(" ")
        check(splitText.size == 2) {
            "Неверный формат команды"
        }

        val actionResult = withUUIDCheck(splitText[0])
        {
            val value = splitText[1].toDoubleOrNull() ?: error("Некорректное значение количества: ${splitText[1]}")

            val request = AddProductEventRqDto(ProductEventDto(ChangeType.REDUCE, value))

            val result = Either.catch {
                purchasesServiceClient.addProductEvent(request, it)
            }

            result.fold({ ex ->
                error("Ошибка от внешнего сервиса: ${ex.message}")
            }, {
                prepareResultWithText("Изменение продукта выполнено", telegramAction)
            })
        }

        sendResult(actionResult)
        listProductsSender.getAndPushProductsList(telegramAction.telegramUserInfo.chatId)

        return actionResult
    }
}