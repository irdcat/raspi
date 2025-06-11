package irdcat.mongo.data

import java.util.UUID

internal data class CollectionDto(
    val name: String,
    val type: String,
    val options: Map<String, Any>,
    val info: Info,
    val idIndex: Index
) {
    data class Info(
        val readOnly: Boolean,
        val uuid: UUID
    )

    data class Index(
        val v: Int,
        val key: Map<String, Any>,
        val name: String
    )
}
