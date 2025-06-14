package irdcat.auth.service

import irdcat.auth.security.UserData

data class UserDataDto(
    val username: String,
    val displayName: String
) {
    companion object {
        fun fromUserData(userData: UserData) =
            UserDataDto(userData.username, userData.displayName)
    }
}
