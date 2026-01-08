package io.kamae.recipes.infrastructure.util

import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.util.*

@Component
class UniqueValueGenerator {
    fun generateId(): UUID = UUID.randomUUID()

    fun currentDateTime(): LocalDateTime = LocalDateTime.now()
}