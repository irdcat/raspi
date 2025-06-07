package irdcat.mongo.status

import com.mongodb.BasicDBObject
import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoDatabase
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Service
internal class ServerStatusService(
    private val mongoClient: MongoClient
) {
    companion object {
        private val SERVER_STATUS_COMMAND = BasicDBObject("serverStatus", 1)
    }

    private fun MongoClient.getAdminDatabase() = getDatabase("admin")

    private fun MongoDatabase.getServerStatus() = runCommand(SERVER_STATUS_COMMAND)

    fun getServerStatus(): Mono<ServerStatusDto> {
        return mongoClient
            .getAdminDatabase()
            .toMono()
            .flatMap { it.getServerStatus().toMono() }
            .map { it.toServerStatusDto() }
    }
}