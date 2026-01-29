package io.kamae.family.bot

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.junit5.WireMockExtension
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(WireMockExtension::class)
abstract class AbstractClientTest : AbstractIntegrationTest() {
    protected val wireMockServer = WireMockServer(9092)

    @BeforeEach
    fun configureWireMock() {
        wireMockServer.start()

        WireMock.configureFor(9092)
    }

    @AfterEach
    fun stopWireMockServer() {
        wireMockServer.stop()
    }
}