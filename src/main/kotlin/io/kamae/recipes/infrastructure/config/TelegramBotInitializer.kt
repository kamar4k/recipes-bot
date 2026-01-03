package io.kamae.recipes.infrastructure.config

import io.kamae.recipes.infrastructure.telegram.bot.RecipesBot
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.context.event.ContextRefreshedEvent
import org.springframework.context.event.EventListener
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession

@Configuration
@Profile("!test")
class TelegramBotInitializer(private val recipesBot: RecipesBot) {
    @EventListener(ContextRefreshedEvent::class)
    fun initBot() {
        TelegramBotsApi(DefaultBotSession::class.java).registerBot(recipesBot)
    }
}