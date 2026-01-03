package io.kamae.recipes

import io.kamae.recipes.infrastructure.config.TelegramBotConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories


@SpringBootApplication
@EnableJpaRepositories
@EnableConfigurationProperties(TelegramBotConfig::class)
class RecipesApplication

fun main(args: Array<String>) {
    runApplication<RecipesApplication>(*args)
}