package irdcat.fitness.controller

import org.springframework.http.server.reactive.ServerHttpRequest
import java.text.SimpleDateFormat

data class ErrorResponse(val exception: String, val message: String, val path: String) {
    companion object {
        private val dateFormat = SimpleDateFormat()

        fun fromThrowable(throwable: Throwable, serverHttpRequest: ServerHttpRequest): ErrorResponse {
            return ErrorResponse(
                throwable::class.simpleName ?: "No exception available",
                throwable.message ?: "No message available",
                serverHttpRequest.path.toString()
            )
        }
    }
}
