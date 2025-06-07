package irdcat.mongo.data

import com.mongodb.reactivestreams.client.MongoClient
import org.springframework.stereotype.Service
import reactor.kotlin.core.publisher.toFlux

@Service
internal class DataService(
    private val mongoClient: MongoClient
) {

    fun getDatabases() = mongoClient.listDatabases()
        .toFlux()
        .map { it.toDatabaseDto() }

    fun getDatabase(name: String) = getDatabases()
        .filter { it.name == name}
        .next()

    fun getCollections(dbName: String) = mongoClient.getDatabase(dbName)
        .listCollections()
        .toFlux()
        .map { it.toCollectionDto() }

    fun getCollection(dbName: String, collectionName: String) = getCollections(dbName)
        .filter { it.name == collectionName}
        .next()
}