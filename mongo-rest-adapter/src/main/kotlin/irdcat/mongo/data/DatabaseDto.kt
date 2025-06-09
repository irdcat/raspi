package irdcat.mongo.data

internal data class DatabaseDto(
    val name: String,
    val sizeOnDisk: Long?,
    val empty: Boolean?
)

