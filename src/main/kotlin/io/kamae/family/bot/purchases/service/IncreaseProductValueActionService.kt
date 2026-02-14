package io.kamae.family.bot.purchases.service

import arrow.core.Either
import io.kamae.family.bot.common.domain.keyboard.CancellationKeyboard
import io.kamae.family.bot.core.api.ActionService.Companion.prepareResultWithText
import io.kamae.family.bot.core.api.MessageHistoryProvider
import io.kamae.family.bot.core.api.TelegramBotMessageSender
import io.kamae.family.bot.core.api.model.MessageHistoryCategory
import io.kamae.family.bot.core.api.model.MessageHistoryElement
import io.kamae.family.bot.core.domain.model.CommandContext
import io.kamae.family.bot.core.domain.model.TelegramAction
import io.kamae.family.bot.core.domain.model.TelegramActionResult
import io.kamae.family.bot.core.service.AbstractActionService
import io.kamae.family.bot.purchases.api.ListProductsSender
import io.kamae.family.bot.purchases.client.PurchasesServiceClient
import io.kamae.family.bot.purchases.client.dto.AddProductEventRqDto
import io.kamae.family.bot.purchases.client.dto.ChangeType
import io.kamae.family.bot.purchases.client.dto.ProductEventDto
import io.kamae.family.bot.purchases.constants.PurchasesConstants
import io.kamae.family.bot.recipes.domain.keyboard.BaseKeyboard
import org.springframework.stereotype.Service
import java.util.*

@Service
class IncreaseProductValueActionService(
    private val purchasesServiceClient: PurchasesServiceClient,
    private val messageHistoryProvider: MessageHistoryProvider,
    private val listProductsSender: ListProductsSender,
    sender: TelegramBotMessageSender,
) : AbstractActionService(sender) {
    companion object {
        private const val INPUT_QUANTITY_QUESTION = "INPUT_QUANTITY"
    }

    override fun executeAction(telegramAction: TelegramAction): TelegramActionResult {
        val lastContextElement = telegramAction.commandContext
            .sequence
            .lastOrNull()
            ?.also {
                if (it.answer?.value == "Отмена") {
                    val result = prepareResultWithText(
                        "Изменение продукта отменено",
                        telegramAction,
                        keyboard = BaseKeyboard
                    )

                    sendResult(result)

                    return result
                }
            }
            ?: return prepareQuantityInput(telegramAction)


        val text = telegramAction.commandContext.text!!

        val actionResult = withUUIDCheck(text) {
            increaseAndGetResult(lastContextElement, it, telegramAction)
        }

        val msgId = sendResult(actionResult)

        messageHistoryProvider.addToHistory(
            telegramAction.getChatId(),
            MessageHistoryCategory(PurchasesConstants.PRODUCT_LIST_CATEGORY),
            MessageHistoryElement(telegramAction.messageId!!), MessageHistoryElement(msgId)
        )


        listProductsSender.getAndPushProductsList(telegramAction.getChatId())

        return actionResult
    }

    private fun increaseAndGetResult(
        lastContextElement: CommandContext.Element,
        productId: UUID,
        telegramAction: TelegramAction
    ): TelegramActionResult =
        lastContextElement.answer
            ?.value
            ?.toDoubleOrNull()
            ?.let { value -> sendProductEventAndGetResult(value, productId, telegramAction) }
            ?: error("Некорректное значение количества: ${lastContextElement.answer?.value}")

    private fun sendProductEventAndGetResult(
        value: Double,
        productId: UUID,
        telegramAction: TelegramAction
    ): TelegramActionResult {
        val request = AddProductEventRqDto(
            ProductEventDto(ChangeType.INCREASE, value)
        )

        val result = Either.catch {
            purchasesServiceClient.addProductEvent(request, productId)
        }

        return result.fold({ ex ->
            error("Ошибка от внешнего сервиса: ${ex.message}")
        }, {
            prepareResultWithText("Изменение продукта выполнено", telegramAction, keyboard = BaseKeyboard)
        })
    }

    private fun prepareQuantityInput(telegramAction: TelegramAction): TelegramActionResult {
        val result = prepareResultWithText(
            "Введите количество",
            telegramAction,
            CommandContext.Question(INPUT_QUANTITY_QUESTION),
            CancellationKeyboard
        )

        val msgId = sendResult(result)

        messageHistoryProvider.addToHistory(
            telegramAction.getChatId(),
            MessageHistoryCategory(PurchasesConstants.PRODUCT_LIST_CATEGORY),
            MessageHistoryElement(msgId)
        )

        return result
    }
}