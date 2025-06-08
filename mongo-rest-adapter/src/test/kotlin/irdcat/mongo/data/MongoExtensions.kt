package irdcat.mongo.data

import com.mongodb.reactivestreams.client.MongoClient
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux

private const val DUMMY_COLLECTION_NAME = "delete_me"
private val DEFAULT_DATABASES = arrayOf("admin", "config", "local")

internal fun MongoClient.purgeNonDefaultDatabases() =
    listDatabaseNames()
        .toFlux()
        .filter { !DEFAULT_DATABASES.contains(it) }
        .map { getDatabase(it).drop() }
        .map { Mono.from(it) }
        .collectList()
        .flatMapMany { Flux.concat(it) }
        .blockLast()
        .let { DEFAULT_DATABASES }
        .forEach { assertDatabaseExists(it) }


internal fun MongoClient.createDatabase(name: String) =
    getDatabase(name)
        .createCollection(DUMMY_COLLECTION_NAME)
        .let { Mono.from(it) }
        .map { assertDatabaseExists(name) }
        .block()

internal fun MongoClient.createCollection(db: String, name: String) =
    getDatabase(db)
        .createCollection(name)
        .let { Mono.from(it) }
        .map { getDatabase(db).getCollection(DUMMY_COLLECTION_NAME) }
        .map { it.drop() }
        .map { Mono.from(it) }
        .map { assertCollectionExists(db, name) }
        .map { assertCollectionDoesNotExists(db, DUMMY_COLLECTION_NAME)}
        .block()