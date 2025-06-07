package irdcat.mongo.status

import java.time.Instant

internal data class ServerStatusDto(
    val host: String,
    val version: String,
    val uptime: Double,
    val localTime: Instant,
    val connections: Connections,
    val globalLock: GlobalLock,
    val opcounters: OperationCounters
) {
    data class Connections(
        val current: Int,
        val available: Int)

    data class GlobalLock(
        val activeClients: ClientMetrics,
        val currentQueue: ClientMetrics) {

        data class ClientMetrics(
            val total: Int,
            val readers: Int,
            val writers: Int)
    }

    data class OperationCounters(
        val insert: Long,
        val query: Long,
        val update: Long,
        val delete: Long)
}
