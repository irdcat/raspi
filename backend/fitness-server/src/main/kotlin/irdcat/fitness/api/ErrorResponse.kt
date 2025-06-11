package irdcat.fitness.api

import org.springframework.http.server.reactive.ServerHttpRequest
import java.util.Date

data class ErrorResponse(
    val timestamp: Date,
    val path: String,
    val status: Int,
    val error: String?,
    val requestId: String
) {
    companion object {
        fun fromThrowable(throwable: Throwable, serverHttpRequest: ServerHttpRequest, status: Int): ErrorResponse {
            return ErrorResponse(
                timestamp = Date(),
                path = serverHttpRequest.uri.toString(),
                status = status,
                error = throwable.message,
                requestId = serverHttpRequest.id
            )
        }
    }
}
