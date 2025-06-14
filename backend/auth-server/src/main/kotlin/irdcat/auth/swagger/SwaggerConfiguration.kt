package irdcat.auth.swagger

import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.OAuthFlow
import io.swagger.v3.oas.models.security.OAuthFlows
import io.swagger.v3.oas.models.security.Scopes
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class SwaggerConfiguration {

    @Bean
    fun openApi() =
        OpenAPI()
            .components(getComponents())
            .info(Info())
            .addSecurityItem(SecurityRequirement().addList("Password Flow"))

    private fun getComponents() =
        SecurityScheme()
            .type(SecurityScheme.Type.OAUTH2)
            .flows(
                OAuthFlows()
                    .password(
                        OAuthFlow()
                            .tokenUrl("http://localhost:8081/oauth/token")
                            .scopes(Scopes().addString("trust", "trust all"))
                    )
            )
            .let {
                Components()
                    .securitySchemes(
                        mapOf("Password Flow" to it)
                    )
            }
}