package io.kamae.family.bot

import io.kamae.family.bot.config.TelegramBotConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients
import org.springframework.data.jpa.repository.config.EnableJpaRepositories


@SpringBootApplication
@EnableJpaRepositories
@EnableConfigurationProperties(TelegramBotConfig::class)
@EnableFeignClients
class FamilyBotApplication

fun main(args: Array<String>) {
    runApplication<FamilyBotApplication>(*args)
}