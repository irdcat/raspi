package irdcat.mongo.data

enum class ErrorCategory(val value: String) {
    NOT_FOUND("Not found")
}

private fun <T> List<T>.shiftRight(offset: Int): List<T> {
    toMutableList().addAll(offset, this)
    return this
}

internal fun toErrorMessage(errorCategory: ErrorCategory, vararg fields: String) =
    fields
        .mapIndexed { idx, it -> "$it${" in".takeUnless { idx == fields.lastIndex }?:""}" }
        .shiftRight(1)
        .toMutableList()
        .apply { add(0, errorCategory.value + ":") }
        .joinToString(" ")

internal open class DataApiException(
    val databaseName: String,
    val collectionName: String? = null,
    val documentId: String? = null,
    val errorCategory: ErrorCategory
): RuntimeException(
    toErrorMessage(
        errorCategory,
        *arrayOf(documentId, collectionName, databaseName)
            .filterNotNull()
            .toTypedArray()
    )
)

internal class DatabaseNotFoundException(name: String):
        DataApiException(
            databaseName = name,
            errorCategory = ErrorCategory.NOT_FOUND
        )

internal class CollectionNotFoundException(dbName: String, colName: String):
        DataApiException(
            databaseName = dbName,
            collectionName = colName,
            errorCategory = ErrorCategory.NOT_FOUND
        )

internal class DocumentNotFoundException(dbName: String, colName: String, documentId: String):
        DataApiException(
            databaseName = dbName,
            collectionName = colName,
            documentId = documentId,
            errorCategory = ErrorCategory.NOT_FOUND
        )