package irdcat.mongo.data

internal data class DataApiError(
    val databaseName: String,
    val collectionName: String?,
    val status: Int,
    val error: String
) {
    companion object {
        fun withStatus(status: Int, ex: DataApiException) =
            DataApiError(ex.databaseName, ex.collectionName, status, ex.errorCategory.value)

        fun from(ex: DataApiException) = when(ex) {
            is DatabaseNotFoundException -> withStatus(404, ex)
            is CollectionNotFoundException -> withStatus(404, ex)
            else -> withStatus(500, ex)
        }
    }
}


