package irdcat.apigw

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureWebTestClient
abstract class BaseApiGatewayTest {

    @Autowired
    private lateinit var webTestClient: WebTestClient

    protected fun webTestClient(): WebTestClient = webTestClient
}