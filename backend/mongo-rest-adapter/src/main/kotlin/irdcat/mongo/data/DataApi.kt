package irdcat.mongo.data

import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@Suppress("unused")
@RequestMapping(
    path = ["/api"],
    produces = [MediaType.APPLICATION_JSON_VALUE]
)
internal class DataApi(
    private val dataService: DataService
) {

    @GetMapping("/database")
    fun getDatabases() =
        dataService.getDatabases()

    @GetMapping("/database/{name}")
    fun getDatabase(@PathVariable name: String) =
        dataService.getDatabase(name)

    @PostMapping("/database/{name}")
    fun addDatabase(@PathVariable name: String) =
        dataService.addDatabase(name)

    @DeleteMapping("/database/{name}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteDatabase(@PathVariable name: String) =
        dataService.deleteDatabase(name)

    @GetMapping("/database/{dbName}/collection")
    fun getCollections(@PathVariable dbName: String) =
        dataService.getCollections(dbName)

    @GetMapping("/database/{dbName}/collection/{collectionName}")
    fun getCollection(@PathVariable dbName: String, @PathVariable collectionName: String) =
        dataService.getCollection(dbName, collectionName)

    @PostMapping("/database/{dbName}/collection/{collectionName}")
    fun addCollection(@PathVariable dbName: String, @PathVariable collectionName: String) =
        dataService.addCollection(dbName, collectionName)

    @DeleteMapping("/database/{dbName}/collection/{collectionName}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteCollection(@PathVariable dbName: String, @PathVariable collectionName: String) =
        dataService.deleteCollection(dbName, collectionName)

    @GetMapping("/database/{dbName}/collection/{collectionName}/document")
    fun getDocuments(@PathVariable dbName: String, @PathVariable collectionName: String) =
        dataService.getDocuments(dbName, collectionName)

    @GetMapping("/database/{dbName}/collection/{collectionName}/document/{id}")
    fun getDocument(@PathVariable dbName: String, @PathVariable collectionName: String, @PathVariable id: String) =
        dataService.getDocument(dbName, collectionName, id)

    @ExceptionHandler(DataApiException::class)
    fun handleDataApiException(ex: DataApiException, response: ServerHttpResponse) =
        DataApiError.from(ex)
            .applyToResponse(response)

    @ExceptionHandler(Exception::class)
    fun handleException(ex: Exception, response: ServerHttpResponse) =
        GenericError(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.message.orEmpty())
            .applyToResponse(response)
}