package irdcat.auth

import irdcat.auth.security.UserData
import irdcat.auth.security.UserRole
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.mongodb.core.MongoTemplate
import org.springframework.security.oauth2.core.AuthorizationGrantType
import org.springframework.security.oauth2.core.ClientAuthenticationMethod
import org.springframework.security.oauth2.core.oidc.OidcScopes
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient
import java.util.UUID

@Profile("!test")
@Configuration
class LocalApplicationInitializer(
    private val mongoTemplate: MongoTemplate,
): InitializingBean {

    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun afterPropertiesSet() {
        mongoTemplate.insert(
            UserData(
                null,
                "test",
                "Test",
                "\$2a\$10\$QHhxZNoReeJoOdFZpN3w1.0WPTkG8Qun3Vv72cEmztLM2E4wPtK5u",
                listOf(UserRole.ADMIN, UserRole.USER)
            )
        ).let { log.info("Added: $it") }

        mongoTemplate.insert(
            RegisteredClient
                .withId(UUID.randomUUID().toString())
                .clientId("test")
                .clientSecret("\$2a\$10\$QHhxZNoReeJoOdFZpN3w1.0WPTkG8Qun3Vv72cEmztLM2E4wPtK5u")
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
                .authorizationGrantType(AuthorizationGrantType.REFRESH_TOKEN)
                .redirectUri("http://localhost:8080/login/oauth2/code/test")
                .postLogoutRedirectUri("http://localhost:8081/bye")
                .scope(OidcScopes.OPENID)
                .scope(OidcScopes.PROFILE)
                .scope(OidcScopes.EMAIL)
                .build(),
            "registeredClientData"
        ).let { log.info("Added: $it") }
    }
}