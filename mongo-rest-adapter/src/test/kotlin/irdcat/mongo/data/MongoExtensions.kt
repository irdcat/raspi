package irdcat.mongo.data

import com.mongodb.reactivestreams.client.MongoClient
import org.bson.Document
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux

private const val DUMMY_COLLECTION_NAME = "delete_me"
private val DEFAULT_DATABASES = listOf("admin", "config", "local")

internal fun MongoClient.purgeNonDefaultDatabases() =
    listDatabaseNames()
        .toFlux()
        .filter { !DEFAULT_DATABASES.contains(it) }
        .map { getDatabase(it).drop() }
        .map { Mono.from(it) }
        .collectList()
        .flatMapMany { Flux.concat(it) }
        .thenMany(Flux.fromIterable(DEFAULT_DATABASES))
        .toIterable()
        .forEach { assertDatabaseExists(it) }


internal fun MongoClient.createDatabase(name: String) =
    getDatabase(name)
        .createCollection(DUMMY_COLLECTION_NAME)
        .let { Mono.from(it) }
        .then(Mono.just(name))
        .block()!!
        .let { assertDatabaseExists(it) }

internal fun MongoClient.createCollection(db: String, name: String) =
    createCollection(db, name, true)

internal fun MongoClient.createCollection(db: String, name: String, cleanupDummy: Boolean) {
    getDatabase(db)
        .createCollection(name)
        .let { Mono.from(it) }
        .block()
        .let { assertCollectionExists(db, name) }

    if (cleanupDummy)
        getDatabase(db).getCollection(DUMMY_COLLECTION_NAME).drop()
            .let { Mono.from(it) }
            .block()
            .run { assertCollectionDoesNotExist(db, DUMMY_COLLECTION_NAME) }
}

internal fun MongoClient.addDocument(db: String, name: String, document: Document) =
    getDatabase(db)
        .getCollection(name)
        .insertOne(document)
        .let { Mono.from(it) }
        .block()
        .run { assertDocumentExists(db, name, document.getId()) }