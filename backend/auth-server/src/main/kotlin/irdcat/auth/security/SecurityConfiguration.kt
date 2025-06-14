package irdcat.auth.security

import com.nimbusds.jose.jwk.source.JWKSource
import com.nimbusds.jose.proc.SecurityContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.security.config.Customizer.withDefaults
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer
import org.springframework.security.oauth2.server.authorization.token.DelegatingOAuth2TokenGenerator
import org.springframework.security.oauth2.server.authorization.token.JwtGenerator
import org.springframework.security.web.authentication.HttpStatusEntryPoint
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler

@Configuration(proxyBeanMethods = false)
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
internal class SecurityConfiguration {

    @Bean
    @Order(1)
    fun authorizationServerFilterChain(http: HttpSecurity) =
        OAuth2AuthorizationServerConfigurer
            .authorizationServer()
            .let { configurer ->
                http
                    .securityMatcher(configurer.endpointsMatcher)
                    .with(configurer) { it.oidc(withDefaults()) }
                    .authorizeHttpRequests {
                        it
                            .requestMatchers("/oauth2/login").permitAll()
                            .requestMatchers("/swagger/**").permitAll()
                            .requestMatchers("/v3/api-docs").permitAll()
                            .anyRequest().authenticated()
                    }
                    .build()
            }

    @Bean
    @Order(2)
    fun defaultFilterChain(http: HttpSecurity) =
        http
            .formLogin {
                it.successHandler { req, res, auth -> res.status = HttpStatus.NO_CONTENT.value() }
                    .failureHandler(SimpleUrlAuthenticationFailureHandler())
            }
            .exceptionHandling {
                it.authenticationEntryPoint(HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
            }
            .build()

    @Bean
    fun passwordEncoder() = BCryptPasswordEncoder(12)

    @Bean
    fun tokenGenerator(
        jwkSource: JWKSource<SecurityContext>,
        oAuth2AccessTokenCustomizer: OAuth2AccessTokenCustomizer
    ) =
        JwtGenerator(NimbusJwtEncoder(jwkSource))
            .apply { setJwtCustomizer(oAuth2AccessTokenCustomizer) }
            .let { DelegatingOAuth2TokenGenerator(it) }
}