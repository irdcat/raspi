package irdcat.auth.security

import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.AuthorityUtils.authorityListToSet
import org.springframework.security.core.userdetails.User
import org.springframework.security.oauth2.server.authorization.OAuth2TokenType.ACCESS_TOKEN
import org.springframework.security.oauth2.server.authorization.token.JwtEncodingContext
import org.springframework.security.oauth2.server.authorization.token.OAuth2TokenCustomizer
import org.springframework.stereotype.Component

@Component
internal class OAuth2AccessTokenCustomizer: OAuth2TokenCustomizer<JwtEncodingContext> {
    override fun customize(context: JwtEncodingContext?) {
        context!!
            .takeIf { ACCESS_TOKEN == it.tokenType }
            ?.let {
                it.claims.claims { claims ->
                    User.builder()
                        .roles()
                    val user = it.getPrincipal<Authentication>().principal as User
                    val roles = authorityListToSet(user.authorities)
                        .map { authority -> authority.replaceFirst("^ROLE_", "") }
                        .toSet()
                    claims.put("roles", roles)
                }
            }
    }
}