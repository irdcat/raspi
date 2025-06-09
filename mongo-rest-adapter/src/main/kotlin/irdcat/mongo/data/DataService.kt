package irdcat.mongo.data

import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoCollection
import com.mongodb.reactivestreams.client.MongoDatabase
import org.bson.Document
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.core.publisher.Mono.error
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono

@Service
internal class DataService(
    private val mongoClient: MongoClient
) {

    companion object {
        private const val DUMMY_COLLECTION_NAME = "delete_me"
    }

    fun getDatabases() =
        mongoClient
            .listDatabases()
            .toFlux()
            .map { it.toDatabaseDto() }

    fun getDatabase(name: String) =
        getDatabases()
            .filter { it.name == name }
            .next()
            .switchIfEmpty(error(DatabaseNotFoundException(name)))

    fun addDatabase(name: String) =
        mongoClient
            .unsafeGetDatabase(name)
            .map { it.createCollection(DUMMY_COLLECTION_NAME) }
            .flatMap { Mono.from(it) }
            .then(name.toMono())
            .flatMap(this::getDatabase)

    fun deleteDatabase(name: String) =
        mongoClient
            .safeGetDatabase(name)
            .map(MongoDatabase::drop)
            .flatMap { Mono.from(it) }

    fun getCollections(dbName: String) =
        mongoClient
            .safeGetDatabase(dbName)
            .flatMapMany { it.listCollections().toFlux() }
            .map { it.toCollectionDto() }

    fun getCollection(dbName: String, collectionName: String) =
        getCollections(dbName)
            .filter { it.name == collectionName }
            .next()
            .switchIfEmpty(error(CollectionNotFoundException(dbName, collectionName)))

    fun addCollection(dbName: String, collectionName: String) =
        mongoClient
            .safeGetDatabase(dbName)
            .map { it.createCollection(collectionName) }
            .flatMap { Mono.from(it) }
            .then(collectionName.toMono())
            .flatMap { getCollection(dbName, it) }

    fun deleteCollection(dbName: String, collectionName: String) =
        mongoClient
            .safeGetCollection(dbName, collectionName)
            .map(MongoCollection<Document>::drop)
            .flatMap { Mono.from(it) }

    fun getDocuments(dbName: String, collectionName: String) =
        mongoClient
            .safeGetCollection(dbName, collectionName)
            .flatMapMany { it.find().toFlux() }

    fun getDocument(dbName: String, collectionName: String, id: String) =
        mongoClient
            .safeGetCollection(dbName, collectionName)
            .flatMap { it.findById(id) }
            .switchIfEmpty(error(DocumentNotFoundException(dbName, collectionName, id)))

    private fun MongoClient.safeGetDatabase(name: String) =
        mongoClient
            .listDatabaseNames()
            .toFlux()
            .filter(name::equals)
            .next()
            .switchIfEmpty(error(DatabaseNotFoundException(name)))
            .map { mongoClient.getDatabase(it) }

    private fun MongoClient.unsafeGetDatabase(name: String) =
        mongoClient
            .getDatabase(name)
            .toMono()

    private fun MongoClient.safeGetCollection(db: String, name: String) =
        safeGetDatabase(db)
            .flatMapMany {
                it.listCollectionNames()
                    .toFlux()
                    .map { name -> name to it }
            }
            .filter { it.first == name }
            .next()
            .switchIfEmpty(error(CollectionNotFoundException(db, name)))
            .map { it.second.getCollection(it.first) }
}