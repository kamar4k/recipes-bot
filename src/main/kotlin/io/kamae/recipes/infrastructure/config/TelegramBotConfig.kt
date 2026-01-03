package io.kamae.recipes.infrastructure.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@ConfigurationProperties(prefix = "bot")
data class TelegramBotConfig(
    val name: String,
    val token: String
)