package io.kamae.family.bot.domain.telegram

data class CommandContext(
    val command: String,
    val text: String?,
    val sequence: List<Element> = emptyList()
) {

    data class Element(val question: Question, val answer: Answer?)

    @JvmInline
    value class Question(val value: String)

    @JvmInline
    value class Answer(val value: String)
}