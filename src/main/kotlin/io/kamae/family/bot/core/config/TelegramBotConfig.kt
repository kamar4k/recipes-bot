package io.kamae.family.bot.core.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "bot")
data class TelegramBotConfig(
    val name: String,
    val token: String
)