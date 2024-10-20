package irdcat.fitness.exception

import org.springframework.http.server.reactive.ServerHttpRequest
import java.text.SimpleDateFormat

data class ErrorMessage(val exception: String, val message: String, val path: String) {
    companion object {
        private val dateFormat = SimpleDateFormat()

        fun fromThrowable(throwable: Throwable, serverHttpRequest: ServerHttpRequest): ErrorMessage {
            return ErrorMessage(
                throwable::class.simpleName ?: "No exception available",
                throwable.message ?: "No message available",
                serverHttpRequest.path.toString()
            )
        }
    }
}
