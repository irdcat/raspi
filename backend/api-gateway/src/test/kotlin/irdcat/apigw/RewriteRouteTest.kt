package irdcat.apigw

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.get
import com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor
import com.github.tomakehurst.wiremock.client.WireMock.ok
import com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo
import org.springframework.test.context.ActiveProfiles
import org.wiremock.spring.ConfigureWireMock
import org.wiremock.spring.EnableWireMock
import org.wiremock.spring.InjectWireMock
import kotlin.test.Test

@ActiveProfiles(profiles = ["rewrite-test", "test"])
@EnableWireMock(
    ConfigureWireMock(name = "test-service", port = 8089)
)
class RewriteRouteTest: BaseApiGatewayTest() {

    @InjectWireMock("test-service")
    private lateinit var testService: WireMockServer

    @Test
    fun pathIsProperlyRewritten() {
        get("/hello")
            .willReturn(ok())
            .let(testService::stubFor)

        webTestClient()
            .get()
            .uri("/test/hello")
            .exchange()
            .expectStatus().isOk

        getRequestedFor(urlPathEqualTo("/hello"))
            .let(testService::verify)
    }

    @Test
    fun longerPathIsProperlyRewritten() {
        get("/hello/world")
            .willReturn(ok())
            .let(testService::stubFor)

        webTestClient()
            .get()
            .uri("/test/hello/world")
            .exchange()
            .expectStatus().isOk

        getRequestedFor(urlPathEqualTo("/hello/world"))
            .let(testService::verify)
    }
}