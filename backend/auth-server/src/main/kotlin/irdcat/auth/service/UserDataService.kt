package irdcat.auth.service

import irdcat.auth.service.exception.UserNotFoundException
import irdcat.auth.security.MongoUserDataRepository
import org.springframework.stereotype.Service

@Service
class UserDataService(
    private val mongoUserDataRepository: MongoUserDataRepository
) {

    fun findById(id: String) = mongoUserDataRepository
        .findById(id)
        .map(UserDataDto.Companion::fromUserData)
        .orElseThrow { UserNotFoundException("User with ID #$id not found") }
}