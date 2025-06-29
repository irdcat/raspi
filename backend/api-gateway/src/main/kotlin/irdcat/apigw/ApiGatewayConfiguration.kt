package irdcat.apigw

import org.slf4j.LoggerFactory
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder
import org.springframework.cloud.gateway.route.builder.filters
import org.springframework.cloud.gateway.route.builder.routes
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(ApiGatewayProperties::class)
class ApiGatewayConfiguration {

    private val log = LoggerFactory.getLogger(this::class.java)

    @Bean
    fun routeLocator(rlb: RouteLocatorBuilder, props: ApiGatewayProperties) = rlb
        .routes {
            props.services.reversed().mapIndexed { index, item ->
                route(item.name) {
                    path("${item.prefix}/**")
                    filters {
                        item.rewriteSpec?.map {
                            rewritePath(it.source, it.target)
                        }
                    }
                    order(index)
                    uri(item.redirectTo)
                }
            }
        }
        .apply {
            routes.subscribe { log.info("Route: {}", it) }
        }
}