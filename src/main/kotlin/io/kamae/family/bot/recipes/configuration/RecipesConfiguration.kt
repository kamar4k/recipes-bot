package io.kamae.family.bot.recipes.configuration

import io.kamae.family.bot.core.factory.CommandRegister
import io.kamae.family.bot.recipes.domain.RecipesUserRole
import io.kamae.family.bot.recipes.enums.RecipesCommand
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.access.hierarchicalroles.RoleHierarchy
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl

@Configuration
class RecipesConfiguration {
    @Bean
    fun recipesCommandsRegister(): CommandRegister {
        return CommandRegister { RecipesCommand.entries }
    }

    @Bean
    fun recipesRoleHierarchy(): RoleHierarchy {
        val hierarchy = """
            ${RecipesUserRole.ROLE_RECIPES_ADMIN} > ${RecipesUserRole.ROLE_RECIPES_EDITOR}
            ${RecipesUserRole.ROLE_RECIPES_EDITOR} > ${RecipesUserRole.ROLE_RECIPES_READER}
            ${RecipesUserRole.ROLE_RECIPES_READER} > ${RecipesUserRole.ROLE_RECIPES_GUEST}
        """.trimIndent()

        return RoleHierarchyImpl.fromHierarchy(hierarchy)
    }
}