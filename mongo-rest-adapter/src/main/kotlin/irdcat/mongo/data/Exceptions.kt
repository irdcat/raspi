package irdcat.mongo.data

enum class ErrorCategory(val value: String) {
    NOT_FOUND("Not found");

    fun toMessage(dbName: String) = "$value $dbName"
    fun toMessage(dbName: String, colName: String?) =
        if (colName.isNullOrBlank()) {
            toMessage(dbName)
        } else {
            "$value $colName in $dbName"
        }
}

internal open class DataApiException(
    val databaseName: String,
    val collectionName: String? = null,
    val errorCategory: ErrorCategory
): RuntimeException(errorCategory.toMessage(databaseName, collectionName))

internal class DatabaseNotFoundException(name: String):
        DataApiException(databaseName = name, errorCategory = ErrorCategory.NOT_FOUND)

internal class CollectionNotFoundException(dbName: String, colName: String):
        DataApiException(dbName, colName, ErrorCategory.NOT_FOUND)