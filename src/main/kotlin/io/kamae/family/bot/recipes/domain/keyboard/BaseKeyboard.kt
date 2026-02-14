package io.kamae.family.bot.recipes.domain.keyboard

import io.kamae.family.bot.core.api.BotKeyboard
import io.kamae.family.bot.core.domain.enums.CoreCommand
import io.kamae.family.bot.purchases.enums.PurchasesCommand
import io.kamae.family.bot.recipes.enums.RecipesCommand
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow

object BaseKeyboard : BotKeyboard {

    private val BASE_KEYBOARD: ReplyKeyboardMarkup = ReplyKeyboardMarkup.builder()
        .keyboardRow(KeyboardRow(listOf(KeyboardButton(RecipesCommand.LIST_RECIPES.alias!!))))
        .keyboardRow(KeyboardRow(listOf(KeyboardButton(RecipesCommand.ADD_RECIPE.alias!!))))
        .keyboardRow(KeyboardRow(listOf(KeyboardButton(PurchasesCommand.CREATE_PRODUCT.alias!!))))
        .keyboardRow(KeyboardRow(listOf(KeyboardButton(PurchasesCommand.LIST_PRODUCTS.alias!!))))
        .keyboardRow(KeyboardRow(listOf(KeyboardButton(CoreCommand.HELP.alias!!))))
        .resizeKeyboard(true)
        .oneTimeKeyboard(false)
        .selective(true)
        .build()

    override fun getKeyboard(): ReplyKeyboardMarkup {
        return BASE_KEYBOARD
    }
}