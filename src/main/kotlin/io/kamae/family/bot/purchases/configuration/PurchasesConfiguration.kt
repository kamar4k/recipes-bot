package io.kamae.family.bot.purchases.configuration

import io.kamae.family.bot.core.factory.CommandRegister
import io.kamae.family.bot.purchases.enums.PurchasesCommand
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class PurchasesConfiguration {
    @Bean
    fun purchasesCommandsRegister(): CommandRegister {
        return CommandRegister { PurchasesCommand.entries }
    }
}