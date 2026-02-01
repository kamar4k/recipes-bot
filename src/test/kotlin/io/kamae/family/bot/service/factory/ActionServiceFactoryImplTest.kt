package io.kamae.family.bot.service.factory

import io.kamae.family.bot.AbstractIntegrationTest
import io.kamae.family.bot.service.*
import io.kamae.family.bot.core.api.ActionService
import io.kamae.family.bot.core.factory.ActionServiceFactory
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertInstanceOf
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import kotlin.reflect.KClass

class ActionServiceFactoryImplTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var actionServiceFactory: ActionServiceFactory

    @ParameterizedTest
    @MethodSource("factoryArguments")
    fun getHandler_success(command: String, expectedClass: KClass<out ActionService>) {
        val handler = actionServiceFactory.getActionService(command)

        assertInstanceOf(expectedClass.java, handler)
    }

    @Test
    fun getHandler_unknownCommand() {
        val unknownCommand = "/unknown"

        val error = assertThrows<IllegalStateException> { actionServiceFactory.getActionService(unknownCommand) }

        assertEquals("Команда $unknownCommand не найдена", error.message)
    }

    fun factoryArguments(): List<Arguments> =
        listOf(
            Arguments.of("/add", AddRecipeActionService::class),
            Arguments.of("/get", GetRecipeActionService::class),
            Arguments.of("/list", ListRecipesActionService::class),
            Arguments.of("/help", HelpActionService::class),
            Arguments.of("/default", DefaultActionService::class)
        )
}