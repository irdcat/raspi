package irdcat.auth.security

import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface MongoUserDataRepository: MongoRepository<UserData, String> {

    fun findByUsername(username: String): Optional<UserData>
}