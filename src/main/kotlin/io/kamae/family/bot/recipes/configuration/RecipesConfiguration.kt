package io.kamae.family.bot.recipes.configuration

import io.kamae.family.bot.core.factory.CommandRegister
import io.kamae.family.bot.recipes.enums.RecipesCommand
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class RecipesConfiguration {
    @Bean
    fun recipesCommandsRegister(): CommandRegister {
        return CommandRegister { RecipesCommand.entries }
    }
}