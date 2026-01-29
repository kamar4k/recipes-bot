package io.kamae.family.bot

import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [FamilyBotApplication::class])
abstract class AbstractIntegrationTest: AbstractTest()