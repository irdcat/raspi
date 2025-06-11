package irdcat.mongo

import org.mockito.stubbing.Stubber
import org.slf4j.LoggerFactory
import org.slf4j.event.Level
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.test.web.reactive.server.EntityExchangeResult
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.WebTestClient.BodyContentSpec
import org.springframework.test.web.reactive.server.WebTestClient.ResponseSpec

@AutoConfigureWebTestClient
abstract class BaseApiTest: BaseIntegrationTest() {
    @Autowired
    private lateinit var webTestClient: WebTestClient

    protected fun webTestClient() = webTestClient

    val log = LoggerFactory.getLogger(this.javaClass)

    fun ResponseSpec.log(level: Level) =
        returnResult(String::class.java)
            .apply { log.makeLoggingEventBuilder(level).log("Response: $method $url $status") }
            .let { this }

    fun ResponseSpec.logInfo() = log(Level.INFO)

    fun BodyContentSpec.log(level: Level) =
        this.consumeWith { it.log(level) }

    fun BodyContentSpec.logInfo() = log(Level.INFO)

    fun EntityExchangeResult<ByteArray>.log(level: Level) =
        responseBody?.let { String(it, Charsets.UTF_8) }
            .orEmpty()
            .let { log.makeLoggingEventBuilder(level).log("Response body: $it") }

    fun <T> Stubber.whenever(mock: T) = `when`(mock)
}