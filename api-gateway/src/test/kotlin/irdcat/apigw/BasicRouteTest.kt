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

@ActiveProfiles(profiles = ["basic-test", "test"])
@EnableWireMock(
    ConfigureWireMock(name = "cookbook-server", port = 8083),
    ConfigureWireMock(name = "food-service", port = 8084),
    ConfigureWireMock(name = "cookbook-ui", port = 3000)
)
class BasicRouteTest: BaseApiGatewayTest() {

    @InjectWireMock("cookbook-server")
    private lateinit var cookbookServer: WireMockServer

    @InjectWireMock("food-service")
    private lateinit var foodService: WireMockServer

    @InjectWireMock("cookbook-ui")
    private lateinit var cookbookUi: WireMockServer

    @Test
    fun trafficIsProperlyDelegated() {
        testCookbookServerTraffic()
        testCookbookUiTraffic()
        testFoodServiceTraffic()
    }

    private fun testCookbookServerTraffic() {
        get("/cookbook/api/hello")
            .willReturn(ok())
            .let(cookbookServer::stubFor)

        webTestClient()
            .get()
            .uri("/cookbook/api/hello")
            .exchange()
            .expectStatus().isOk

        val verification = getRequestedFor(urlPathEqualTo("/cookbook/api/hello"))
        verification.let { cookbookServer.verify(1, it) }
        verification.let { foodService.verify(0, it) }
        verification.let { cookbookUi.verify(0, it) }
    }

    private fun testCookbookUiTraffic() {
        get("/cookbook/hello")
            .willReturn(ok())
            .let(cookbookUi::stubFor)

        webTestClient()
            .get()
            .uri("/cookbook/hello")
            .exchange()
            .expectStatus().isOk

        val verification = getRequestedFor(urlPathEqualTo("/cookbook/hello"))
        verification.let { cookbookServer.verify(0, it) }
        verification.let { foodService.verify(0, it) }
        verification.let { cookbookUi.verify(1, it) }
    }

    private fun testFoodServiceTraffic() {
        get("/food/hello")
            .willReturn(ok())
            .let(foodService::stubFor)

        webTestClient()
            .get()
            .uri("/food/hello")
            .exchange()
            .expectStatus().isOk

        val verification = getRequestedFor(urlPathEqualTo("/food/hello"))
        verification.let { cookbookServer.verify(0, it) }
        verification.let { foodService.verify(1, it) }
        verification.let { cookbookUi.verify(0, it) }
    }
}