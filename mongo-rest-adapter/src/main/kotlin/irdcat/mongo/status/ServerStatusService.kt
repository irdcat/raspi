package irdcat.mongo.status

import com.mongodb.BasicDBObject
import com.mongodb.reactivestreams.client.MongoClient
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Service
internal class ServerStatusService(
    private val mongoClient: MongoClient
) {
    private val SERVER_STATUS_COMMAND = BasicDBObject("serverStatus", 1)

    fun serverStatus(): Mono<ServerStatus> {
        return mongoClient.getDatabase("admin")
            .toMono()
            .flatMap { it.runCommand(SERVER_STATUS_COMMAND).toMono() }
            .map { it.toServerStatus() }
    }
}