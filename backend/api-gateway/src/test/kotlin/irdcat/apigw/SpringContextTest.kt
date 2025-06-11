package irdcat.apigw

import kotlin.test.Test
import org.springframework.test.context.ActiveProfiles

@ActiveProfiles(profiles = ["test", "basic-test"])
class SpringContextTest: BaseApiGatewayTest() {

	@Test
	fun contextLoads() {
	}
}
