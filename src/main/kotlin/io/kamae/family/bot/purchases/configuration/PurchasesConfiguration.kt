package io.kamae.family.bot.purchases.configuration

import io.kamae.family.bot.core.factory.CommandRegister
import io.kamae.family.bot.core.security.BotUserRole
import io.kamae.family.bot.core.security.hierarchy.BotAppRoleHierarchy
import io.kamae.family.bot.purchases.enums.PurchasesCommand
import io.kamae.family.bot.purchases.enums.PurchasesUserRole
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class PurchasesConfiguration {
    @Bean
    fun purchasesCommandsRegister(): CommandRegister {
        return CommandRegister { PurchasesCommand.entries }
    }

    @Bean
    fun purchasesAppRoleHierarchy(): BotAppRoleHierarchy {
        return BotAppRoleHierarchy.builder()
            .segment(BotUserRole.ROLE_ADMIN.name, PurchasesUserRole.ROLE_PURCHASES_ADMIN.name)
            .segment(PurchasesUserRole.ROLE_PURCHASES_ADMIN.name, PurchasesUserRole.ROLE_PURCHASES_EDITOR.name)
            .segment(PurchasesUserRole.ROLE_PURCHASES_EDITOR.name, PurchasesUserRole.ROLE_PURCHASES_READER.name)
            .segment(PurchasesUserRole.ROLE_PURCHASES_READER.name, BotUserRole.ROLE_GUEST.name)
            .build()
    }
}