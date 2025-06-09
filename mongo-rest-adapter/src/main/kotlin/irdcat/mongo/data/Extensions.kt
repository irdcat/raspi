package irdcat.mongo.data

import com.mongodb.client.model.Filters
import com.mongodb.reactivestreams.client.MongoCollection
import org.bson.Document
import org.bson.UuidRepresentation
import org.bson.internal.UuidHelper
import org.bson.types.Binary
import reactor.core.publisher.Mono

private const val NAME = "name"
private const val TYPE = "type"
private const val SIZE_ON_DISK = "sizeOnDisk"
private const val EMPTY = "empty"
private const val OPTIONS = "options"
private const val INFO = "info"
private const val ID_INDEX = "idIndex"
private const val READ_ONLY = "readOnly"
private const val UUID = "uuid"
private const val V = "v"
private const val KEY = "key"
private const val ID = "_id"

internal fun Document.toDatabaseDto() =
    DatabaseDto(
        name = getString(NAME),
        sizeOnDisk = getLong(SIZE_ON_DISK),
        empty = getBoolean(EMPTY)
    )

internal fun Document.toCollectionDto() =
    CollectionDto(
        name = getString(NAME),
        type = getString(TYPE),
        options = get(OPTIONS, Document::class.java),
        info = get(INFO, Document::class.java)
            .toCollectionInfo(),
        idIndex = get(ID_INDEX, Document::class.java)
            .toCollectionIndex()
    )

internal fun Document.toCollectionInfo() =
    CollectionDto.Info(
        readOnly = getBoolean(READ_ONLY),
        uuid = get(UUID, Binary::class.java)
            .toUuid()
    )

internal fun Document.toCollectionIndex() =
    CollectionDto.Index(
        v = getInteger(V),
        name = getString(NAME),
        key = get(KEY, Document::class.java)
    )

internal fun Binary.toUuid() =
    UuidHelper.decodeBinaryToUuid(data, type, UuidRepresentation.JAVA_LEGACY)

internal fun <T> MongoCollection<T>.findById(id: String) =
    find(Filters.eq(ID, id)).let { Mono.from(it) }