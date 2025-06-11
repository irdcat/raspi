package irdcat.mongo.data

import org.springframework.http.HttpStatus
import org.springframework.http.server.reactive.ServerHttpResponse

internal abstract class BaseError(val status: Int) {
    fun applyToResponse(serverHttpResponse: ServerHttpResponse) =
        apply { serverHttpResponse.statusCode = HttpStatus.valueOf(status) }
}

internal class DataApiError(
    val databaseName: String,
    val collectionName: String? = null,
    val documentId: String? = null,
    status: Int,
    val error: String
): BaseError(status) {
    companion object {
        private fun withStatus(status: HttpStatus, ex: DataApiException) =
            DataApiError(
                ex.databaseName,
                ex.collectionName,
                ex.documentId,
                status.value(),
                ex.errorCategory.value
            )

        fun from(ex: DataApiException) = when(ex) {
            is DatabaseNotFoundException -> withStatus(HttpStatus.NOT_FOUND, ex)
            is CollectionNotFoundException -> withStatus(HttpStatus.NOT_FOUND, ex)
            is DocumentNotFoundException -> withStatus(HttpStatus.NOT_FOUND, ex)
            else -> withStatus(HttpStatus.INTERNAL_SERVER_ERROR, ex)
        }
    }
}

@Suppress("unused")
internal class GenericError(
    status: Int,
    val message: String
): BaseError(status)