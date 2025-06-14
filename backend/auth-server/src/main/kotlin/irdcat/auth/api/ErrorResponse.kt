package irdcat.auth.api

data class ErrorResponse(
    val message: String
) {
    companion object {
        fun fromThrowable(t: Throwable) = ErrorResponse(t.message ?: "Unknown error")
    }
}
