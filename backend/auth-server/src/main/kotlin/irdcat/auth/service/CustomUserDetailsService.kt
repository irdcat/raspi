package irdcat.auth.service

import irdcat.auth.security.MongoUserDataRepository
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.User
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class CustomUserDetailsService(
    private val mongoUserDataRepository: MongoUserDataRepository
): UserDetailsService {

    override fun loadUserByUsername(username: String?) =
        username
            ?.let { mongoUserDataRepository.findByUsername(username) }
            ?.map {
                User(
                    it.username,
                    it.password,
                    it.roles.map { r -> SimpleGrantedAuthority("ROLE_${r.name}") }
                )
            }
            ?.orElse(null)
}