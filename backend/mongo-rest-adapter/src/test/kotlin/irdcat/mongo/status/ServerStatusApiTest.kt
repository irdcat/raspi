package irdcat.mongo.status

import com.mongodb.MongoClientException
import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoDatabase
import irdcat.mongo.BaseApiTest
import org.hamcrest.core.IsEqual.equalTo
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.doReturn
import org.mockito.Mockito.doThrow
import org.mockito.Mockito.mock
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import kotlin.test.Test
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean

internal class ServerStatusApiTest: BaseApiTest() {

    @MockitoSpyBean
    private lateinit var mongoClient: MongoClient

    @Test
    fun getServerStatus_returnOk() {
        webTestClient()
            .get()
            .uri("/api/status")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .logInfo()
            .expectStatus().isOk
            .expectHeader().value(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .expectBody()
            .logInfo()
            .jsonPath("$.host").isNotEmpty
            .jsonPath("$.version").isNotEmpty
            .jsonPath("$.uptime").isNumber
            .jsonPath("$.localTime").isNotEmpty
            .jsonPath("$.connections").exists()
            .jsonPath("$.connections.current").isNumber
            .jsonPath("$.connections.available").isNumber
            .jsonPath("$.globalLock").exists()
            .jsonPath("$.globalLock.activeClients").exists()
            .jsonPath("$.globalLock.activeClients.total").isNumber
            .jsonPath("$.globalLock.activeClients.readers").isNumber
            .jsonPath("$.globalLock.activeClients.writers").isNumber
            .jsonPath("$.globalLock.currentQueue").exists()
            .jsonPath("$.globalLock.currentQueue.total").isNumber
            .jsonPath("$.globalLock.currentQueue.readers").isNumber
            .jsonPath("$.globalLock.currentQueue.writers").isNumber
            .jsonPath("$.opcounters").exists()
            .jsonPath("$.opcounters.insert").isNumber
            .jsonPath("$.opcounters.query").isNumber
            .jsonPath("$.opcounters.update").isNumber
            .jsonPath("$.opcounters.delete").isNumber
    }

    @Test
    fun getServerStatus_returnInternalServerError() {
        val databaseMock = mock<MongoDatabase>()
        doReturn(databaseMock)
            .whenever(mongoClient)
            .getDatabase(anyString())
        doThrow(MongoClientException("Test mongo exception!"))
            .whenever(databaseMock)
            .runCommand(any())

        webTestClient()
            .get()
            .uri("/api/status")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .logInfo()
            .expectStatus().is5xxServerError
            .expectHeader().value(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .expectBody()
            .logInfo()
            .jsonPath("$.status").isEqualTo(500)
            .jsonPath("$.message").isEqualTo("Test mongo exception!")
    }
}