package io.kamae.family.bot.core.domain.model

import org.telegram.telegrambots.meta.api.objects.Update

data class TelegramUpdateEvent(
    val update: Update
)
