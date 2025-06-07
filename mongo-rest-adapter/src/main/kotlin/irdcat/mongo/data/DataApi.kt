package irdcat.mongo.data

import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
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

    @GetMapping("/database/{dbName}/collection")
    fun getCollections(@PathVariable dbName: String) =
        dataService.getCollections(dbName)

    @GetMapping("/database/{dbName}/collection/{collectionName}")
    fun getCollection(@PathVariable dbName: String, @PathVariable collectionName: String) =
        dataService.getCollection(dbName, collectionName)
}