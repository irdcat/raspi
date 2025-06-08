package irdcat.mongo.data

import com.mongodb.reactivestreams.client.MongoClient
import org.bson.Document
import reactor.kotlin.core.publisher.toFlux
import reactor.test.StepVerifier

internal fun Document.getName() = get("name", String::class.java)

internal fun MongoClient.getAndFilterDatabaseNames(name: String) =
    listDatabases().toFlux()
        .map { it.getName() }
        .filter(name::equals)

internal fun MongoClient.getAndFilterCollectionNames(db: String, name: String) =
    getAndFilterDatabaseNames(db)
        .map { getDatabase(it) }
        .map { it.listCollections() }
        .flatMap { it.toFlux() }
        .map { it.getName() }
        .filter(name::equals)


internal fun MongoClient.assertDatabaseExists(name: String) =
    getAndFilterDatabaseNames(name)
        .let(StepVerifier::create)
        .expectNext(name)
        .verifyComplete()

internal fun MongoClient.assertDatabaseDoesNotExist(name: String) =
    getAndFilterDatabaseNames(name)
        .let(StepVerifier::create)
        .verifyComplete()

internal fun MongoClient.assertCollectionExists(db: String, name: String) =
    getAndFilterCollectionNames(db, name)
        .let(StepVerifier::create)
        .expectNext(name)
        .verifyComplete()

internal fun MongoClient.assertCollectionDoesNotExists(db: String, name: String) =
    getAndFilterCollectionNames(db, name)
        .let(StepVerifier::create)
        .verifyComplete()