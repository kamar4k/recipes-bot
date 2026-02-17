package io.kamae.family.bot.purchases.service

import arrow.core.Either
import io.kamae.family.bot.common.domain.keyboard.CancellationKeyboard
import io.kamae.family.bot.core.api.ActionService.Companion.prepareResultWithText
import io.kamae.family.bot.core.api.TelegramBotMessageSender
import io.kamae.family.bot.core.domain.model.CommandContext
import io.kamae.family.bot.core.domain.model.TelegramAction
import io.kamae.family.bot.core.domain.model.TelegramActionResult
import io.kamae.family.bot.core.domain.model.TelegramResponse
import io.kamae.family.bot.core.service.AbstractDefaultActionService
import io.kamae.family.bot.purchases.api.ListProductsSender
import io.kamae.family.bot.purchases.client.PurchasesServiceClient
import io.kamae.family.bot.purchases.client.dto.ChangeType
import io.kamae.family.bot.purchases.client.dto.CreateProductRqDto
import io.kamae.family.bot.purchases.client.dto.ProductEventDto
import io.kamae.family.bot.recipes.domain.keyboard.BaseKeyboard
import org.springframework.stereotype.Service

//TODO авторизация
@Service
class CreateProductActionService(
    private val purchasesServiceClient: PurchasesServiceClient,
    private val listProductsSender: ListProductsSender,
    sender: TelegramBotMessageSender
) : AbstractDefaultActionService(sender) {
    override fun executeAndGetResult(telegramAction: TelegramAction): TelegramActionResult {
        val lastContextElement = telegramAction.commandContext
            .sequence
            .lastOrNull()
            ?.also {
                if (it.answer?.value == "Отмена") return prepareResultWithText(
                    "Создание продукта отменено",
                    telegramAction,
                    keyboard = BaseKeyboard
                )
            }
            ?: return prepareNameInput(telegramAction)

        return when (CreateProductQuestion.valueOf(lastContextElement.question.value)) {
            CreateProductQuestion.INPUT_NAME -> prepareQuantityInput(telegramAction)
            CreateProductQuestion.INPUT_QUANTITY -> {
                if (lastContextElement.answer?.value?.toDoubleOrNull() == null) {
                    prepareResultWithText("Ожидается ввод числа. Добавление прервано", telegramAction)
                } else {
                    createProductAndPrepareResponse(telegramAction)
                }
            }
        }
    }

    private fun prepareNameInput(telegramAction: TelegramAction): TelegramActionResult {
        return prepareResultWithText(
            "Введите наименование продукта",
            telegramAction,
            CreateProductQuestion.INPUT_NAME.asQuestion(),
            CancellationKeyboard
        )
    }

    private fun prepareQuantityInput(telegramAction: TelegramAction): TelegramActionResult {
        return prepareResultWithText(
            "Введите количество",
            telegramAction,
            CreateProductQuestion.INPUT_QUANTITY.asQuestion(),
            CancellationKeyboard
        )
    }

    private fun createProductAndPrepareResponse(telegramAction: TelegramAction): TelegramActionResult {
        val productDto = parseProduct(telegramAction.commandContext)

        val result = Either.catch { purchasesServiceClient.createProduct(productDto) }

        return result.fold(
            {
                prepareResultWithText(
                    "Неизвестная ошибка: ${it.message}",
                    telegramAction,
                    keyboard = BaseKeyboard
                )
            },
            {
                TelegramActionResult(
                    TelegramResponse(
                        "Продукт успешно добавлен",
                        telegramAction.getChatId(),
                        keyboard = BaseKeyboard
                    )
                ).also {
                    listProductsSender.getAndPushProductsList(telegramAction.getChatId())
                }
            }
        )
    }

    private fun parseProduct(commandContext: CommandContext): CreateProductRqDto {
        val name = commandContext.sequence
            .first { CreateProductQuestion.INPUT_NAME.isQuestion(it.question) }
            .answer!!
            .value

        val value = commandContext.sequence
            .first { CreateProductQuestion.INPUT_QUANTITY.isQuestion(it.question) }
            .answer!!
            .value
            .toDouble()

        return CreateProductRqDto(
            name, ProductEventDto(ChangeType.INCREASE, value)
        )
    }

    private enum class CreateProductQuestion {
        INPUT_NAME,
        INPUT_QUANTITY;

        fun asQuestion() = CommandContext.Question(this.name)
        fun isQuestion(question: CommandContext.Question) = this.name == question.value
    }
}