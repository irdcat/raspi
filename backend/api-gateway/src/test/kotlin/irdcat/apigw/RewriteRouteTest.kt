package irdcat.apigw

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.github.tomakehurst.wiremock.client.WireMock.equalTo
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.ok
import com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.ActiveProfiles
import org.wiremock.spring.ConfigureWireMock
import org.wiremock.spring.EnableWireMock
import org.wiremock.spring.InjectWireMock
import kotlin.test.Test

@ActiveProfiles(profiles = ["rewrite-test", "test"])
@EnableWireMock(
    ConfigureWireMock(name = "test-service", port = 8089)
)
@DirtiesContext
class RewriteRouteTest: BaseApiGatewayTest() {

    @InjectWireMock("test-service")
    private lateinit var testService: WireMockServer

    @Test
    fun rewriteGetDatabases() {
        get("/api/database")
            .willReturn(ok())
            .let(testService::stubFor)

        webTestClient()
            .get()
            .uri("/test/api/database")
            .exchange()
            .expectStatus().isOk

        getRequestedFor(urlPathEqualTo("/api/database"))
            .let(testService::verify)
    }

    @Test
    fun rewriteGetDatabase() {
        get("/api/database/test")
            .willReturn(ok())
            .let(testService::stubFor)

        webTestClient()
            .get()
            .uri("/test/api/database/test")
            .exchange()
            .expectStatus().isOk

        getRequestedFor(urlPathEqualTo("/api/database/test"))
            .let(testService::verify)
    }

    @Test
    fun rewriteGetCollections() {
        get("/api/database/test/collection")
            .willReturn(ok())
            .let(testService::stubFor)

        webTestClient()
            .get()
            .uri("/test/api/database/test/collection")
            .exchange()
            .expectStatus().isOk

        getRequestedFor(urlPathEqualTo("/api/database/test/collection"))
            .let(testService::verify)
    }

    @Test
    fun rewriteGetCollection() {
        get("/api/database/test/collection/test")
            .willReturn(ok())
            .let(testService::stubFor)

        webTestClient()
            .get()
            .uri("/test/api/database/test/collection/test")
            .exchange()
            .expectStatus().isOk

        getRequestedFor(urlPathEqualTo("/api/database/test/collection/test"))
            .let(testService::verify)
    }

    @Test
    fun rewriteGetDocuments() {
        get("/api/database/test/collection/test/document")
            .willReturn(ok())
            .let(testService::stubFor)

        webTestClient()
            .get()
            .uri("/test/api/database/test/collection/test/document")
            .exchange()
            .expectStatus().isOk

        getRequestedFor(urlPathEqualTo("/api/database/test/collection/test/document"))
            .let(testService::verify)
    }

    @Test
    fun rewriteGetDocument() {
        get("/api/database/test/collection/test/document/test")
            .willReturn(ok())
            .let(testService::stubFor)

        webTestClient()
            .get()
            .uri("/test/api/database/test/collection/test/document/test")
            .exchange()
            .expectStatus().isOk

        getRequestedFor(urlPathEqualTo("/api/database/test/collection/test/document/test"))
            .let(testService::verify)
    }

    @Test
    fun rewriteGetDocument_queryParam() {
        get(urlEqualTo("/api/database/test/collection/test/document/test?test=1234"))
            .willReturn(ok())
            .let(testService::stubFor)

        webTestClient()
            .get()
            .uri { builder ->
                builder.path("/test/api/database/test/collection/test/document/test")
                    .queryParam("test", "1234")
                    .build()
            }
            .exchange()
            .expectStatus().isOk

        getRequestedFor(urlEqualTo("/api/database/test/collection/test/document/test?test=1234"))
            .let(testService::verify)
    }
}