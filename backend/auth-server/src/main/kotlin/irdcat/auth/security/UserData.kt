package irdcat.auth.security

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class UserData(
    @field:Id
    val id: String?,
    val username: String,
    val displayName: String,
    val password: String,
    val roles: Collection<UserRole>
) {

    override fun toString(): String {
        return StringBuilder()
            .append(this::class.simpleName)
            .append(" [")
                .append("username=").append(username).append(", ")
                .append("password=[PROTECTED], ")
                .append("displayName=").append(displayName)
            .append(']')
            .toString()
    }
}