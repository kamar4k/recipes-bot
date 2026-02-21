package io.kamae.family.bot.recipes.configuration

import io.kamae.family.bot.core.factory.CommandRegister
import io.kamae.family.bot.core.security.BotUserRole
import io.kamae.family.bot.core.security.hierarchy.BotAppRoleHierarchy
import io.kamae.family.bot.recipes.domain.RecipesUserRole
import io.kamae.family.bot.recipes.enums.RecipesCommand
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RecipesConfiguration {
    @Bean
    fun recipesCommandsRegister(): CommandRegister {
        return CommandRegister { RecipesCommand.entries }
    }

    @Bean
    fun recipesAppRoleHierarchy(): BotAppRoleHierarchy {
        return BotAppRoleHierarchy.builder()
            .segment(BotUserRole.ROLE_ADMIN.name, RecipesUserRole.ROLE_RECIPES_ADMIN.name)
            .segment(RecipesUserRole.ROLE_RECIPES_ADMIN.name, RecipesUserRole.ROLE_RECIPES_EDITOR.name)
            .segment(RecipesUserRole.ROLE_RECIPES_EDITOR.name, RecipesUserRole.ROLE_RECIPES_READER.name)
            .segment(RecipesUserRole.ROLE_RECIPES_READER.name, BotUserRole.ROLE_GUEST.name)
            .build()
    }
}