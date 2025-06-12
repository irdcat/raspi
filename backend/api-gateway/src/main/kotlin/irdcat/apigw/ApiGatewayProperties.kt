package irdcat.apigw

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("application")
data class ApiGatewayProperties(
    val services: Collection<Service>
) {
    data class RewriteSpec(
        val source: String,
        val target: String
    )

    data class Service(
        val name: String,
        val prefix: String,
        val redirectTo: String,
        val rewriteSpec: Array<RewriteSpec>?
    )
}