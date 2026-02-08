package io.kamae.family.bot.core.service

import io.kamae.family.bot.AbstractTest
import io.kamae.family.bot.common.domain.keyboard.CancellationKeyboard
import io.kamae.family.bot.core.api.TelegramBotMessageSender
import io.kamae.family.bot.recipes.domain.keyboard.BaseKeyboard
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.telegram.telegrambots.meta.api.methods.BotApiMethod
import java.io.Serializable

abstract class AbstractDefaultActionServiceTest: AbstractTest() {
    @MockK
    protected lateinit var telegramBotMessageSender: TelegramBotMessageSender

    @BeforeEach
    fun mockSender() {
        every { telegramBotMessageSender.sendMessage(any<BotApiMethod<Serializable>>()) } returns mockk()
    }

    protected fun verifySenderBase(message: String) {
        verify {
            telegramBotMessageSender.sendMessage(
                baseMessageBuilder(message).replyMarkup(BaseKeyboard.getKeyboard()).build()
            )
        }
    }

    protected fun verifySenderCancellation(message: String) {
        verify {
            telegramBotMessageSender.sendMessage(
                baseMessageBuilder(message).replyMarkup(CancellationKeyboard.getKeyboard()).build()
            )
        }
    }

    protected fun verifySenderOnlyMessage(message: String) {
        verify {
            telegramBotMessageSender.sendMessage(
                baseMessageBuilder(message).build()
            )
        }
    }
}