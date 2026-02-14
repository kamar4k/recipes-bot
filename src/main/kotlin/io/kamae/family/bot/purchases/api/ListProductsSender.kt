package io.kamae.family.bot.purchases.api

import io.kamae.family.bot.core.domain.model.TelegramActionResult

interface ListProductsSender {
    fun getAndPushProductsList(chatId: Long): TelegramActionResult
}