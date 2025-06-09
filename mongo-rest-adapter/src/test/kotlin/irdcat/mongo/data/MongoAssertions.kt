package irdcat.mongo.data

import com.mongodb.reactivestreams.client.MongoClient
import org.bson.Document
import reactor.kotlin.core.publisher.toFlux
import reactor.test.StepVerifier

private const val NAME = "name"
private const val ID = "_id"

internal fun Document.getName() = get(NAME, String::class.java)
internal fun Document.getId() = get(ID, String::class.java)

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

internal fun MongoClient.getAndFilterDocumentIds(db: String, name: String, id: String) =
    getAndFilterCollectionNames(db, name)
        .map { getDatabase(db).getCollection(name) }
        .flatMap { it.findById(id) }
        .map { it.getId() }

internal fun MongoClient.assertDatabaseExists(name: String) =
    getAndFilterDatabaseNames(name)
        .let(StepVerifier::create)
        .expectNext(name)
        .verifyComplete()
        .let {}

internal fun MongoClient.assertDatabaseDoesNotExist(name: String) =
    getAndFilterDatabaseNames(name)
        .let(StepVerifier::create)
        .expectNextCount(0)
        .verifyComplete()
        .let {}

internal fun MongoClient.assertCollectionExists(db: String, name: String) =
    getAndFilterCollectionNames(db, name)
        .let(StepVerifier::create)
        .expectNext(name)
        .verifyComplete()
        .let {}

internal fun MongoClient.assertCollectionDoesNotExist(db: String, name: String) =
    getAndFilterCollectionNames(db, name)
        .let(StepVerifier::create)
        .verifyComplete()
        .let {}

internal fun MongoClient.assertDocumentExists(db: String, name: String, id: String) =
    getAndFilterDocumentIds(db, name, id)
        .let(StepVerifier::create)
        .expectNext(id)
        .verifyComplete()
        .let {}

internal fun MongoClient.assertDocumentDoesNotExist(db: String, name: String, id: String) =
    getAndFilterDocumentIds(db, name, id)
        .let(StepVerifier::create)
        .expectNextCount(0)
        .verifyComplete()
        .let {}