package io.kamae.recipes.infrastructure.telegram.handler.factory

import io.kamae.recipes.AbstractIntegrationTest
import io.kamae.recipes.infrastructure.telegram.handler.*
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.Arguments
import org.junit.jupiter.params.provider.MethodSource
import org.springframework.beans.factory.annotation.Autowired
import kotlin.reflect.KClass

class TelegramBotHandlerFactoryImplTest : AbstractIntegrationTest() {

    @Autowired
    private lateinit var telegramBotHandlerFactory: TelegramBotHandlerFactory

    @ParameterizedTest
    @MethodSource("factoryArguments")
    fun getHandler_success(command: String, expectedClass: KClass<out TelegramBotHandler>) {
        val handler = telegramBotHandlerFactory.getHandler(command)

        assertInstanceOf(expectedClass.java, handler)
    }

    @Test
    fun getHandler_unknownCommand() {
        val unknownCommand = "/unknown"

        val error = assertThrows<IllegalStateException> { telegramBotHandlerFactory.getHandler(unknownCommand) }

        assertEquals("Команда $unknownCommand не найдена", error.message)
    }

    fun factoryArguments(): List<Arguments> =
        listOf(
            Arguments.of("/add", AddRecipeHandler::class),
            Arguments.of("/get", GetRecipeHandler::class),
            Arguments.of("/list", ListRecipesHandler::class),
            Arguments.of("/help", HelpHandler::class),
            Arguments.of("/default", DefaultHandler::class)
        )
}