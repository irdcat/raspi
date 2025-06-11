package irdcat.mongo.status

import org.bson.Document
import java.util.Date

internal fun Document.toConnections() =
    ServerStatusDto.Connections(
        current = getInteger("current"),
        available = getInteger("available")
    )

internal fun Document.toClientMetrics() =
    ServerStatusDto.GlobalLock.ClientMetrics(
        total = getInteger("total"),
        readers = getInteger("readers"),
        writers = getInteger("writers")
    )

internal fun Document.toGlobalLock() =
    ServerStatusDto.GlobalLock(
        activeClients = get("activeClients", Document::class.java)
            .toClientMetrics(),
        currentQueue = get("currentQueue", Document::class.java)
            .toClientMetrics()
    )

internal fun Document.toOperationCounters() =
    ServerStatusDto.OperationCounters(
        insert = getLong("insert"),
        query = getLong("query"),
        update = getLong("update"),
        delete = getLong("delete")
    )

internal fun Document.toServerStatusDto() =
    ServerStatusDto(
        host = getString("host"),
        version = getString("version"),
        uptime = getDouble("uptime"),
        localTime = get("localTime", Date::class.java)
            .toInstant(),
        connections = get("connections", Document::class.java)
            .toConnections(),
        globalLock = get("globalLock", Document::class.java)
            .toGlobalLock(),
        opcounters = get("opcounters", Document::class.java)
            .toOperationCounters()
    )