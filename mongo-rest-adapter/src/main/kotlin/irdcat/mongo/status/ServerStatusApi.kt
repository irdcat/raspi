package irdcat.mongo.status

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@Suppress("unused")
@RequestMapping(
    path = ["/api/status"],
    produces = [MediaType.APPLICATION_JSON_VALUE])
internal class ServerStatusApi(
    private val serverStatusService: ServerStatusService
) {

    @GetMapping
    fun getServerStatus() = serverStatusService.getServerStatus()
}