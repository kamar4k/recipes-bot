package io.kamae.family.bot.core.provider.context

import io.kamae.family.bot.core.domain.model.CommandContext

data class MapCommandContext(
    val command: String,
    val text: String?,
    var nextQuestion: String? = null
) {
    private val sequence: MutableList<Element> = mutableListOf()

    fun addToSequence(element: Element) {
        sequence.add(element)
    }

    fun toCommandContext() = CommandContext(
        this.command,
        this.text,
        this.sequence.map { CommandContext.Element(CommandContext.Question(it.question), CommandContext.Answer(it.answer)) }
    )

    data class Element(val question: String, val answer: String)

    companion object {
        fun fromContext(commandContext: CommandContext) = MapCommandContext(
            commandContext.command,
            commandContext.text
        )
    }
}