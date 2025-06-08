package irdcat.mongo.data

import com.mongodb.reactivestreams.client.MongoClient
import irdcat.mongo.BaseApiTest
import org.hamcrest.Matchers.equalTo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import kotlin.test.BeforeTest
import kotlin.test.Test

internal class DataApiTest: BaseApiTest() {

    companion object {
        private const val TEST_DB_NAME = "spring-test"
        private const val TEST_COLLECTION_NAME = "collection-test"
    }

    @Autowired
    private lateinit var mongoClient: MongoClient

    @BeforeTest
    fun setUp() {
        mongoClient.purgeNonDefaultDatabases()
    }

    @Test
    fun getDatabases_returnOk() {
        webTestClient()
            .get()
            .uri("/api/database")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .logInfo()
            .expectStatus().isOk
            .expectHeader().value(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .expectBody()
            .logInfo()
            .jsonPath("$").isArray
            .jsonPath("$[0]").exists()
            .jsonPath("$[0].name").isEqualTo("admin")
            .jsonPath("$[0].sizeOnDisk").isNumber
            .jsonPath("$[0].empty").isBoolean
            .jsonPath("$[1]").exists()
            .jsonPath("$[2]").exists()
            .jsonPath("$[3]").doesNotExist()
    }

    @Test
    fun getDatabase_returnOk() {
        mongoClient.createDatabase(TEST_DB_NAME)

        webTestClient()
            .get()
            .uri("/api/database/$TEST_DB_NAME")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .logInfo()
            .expectStatus().isOk
            .expectHeader().value(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .expectBody()
            .logInfo()
            .jsonPath("$.name").isEqualTo(TEST_DB_NAME)
            .jsonPath("$.sizeOnDisk").isNumber
            .jsonPath("$.empty").isBoolean
    }

    @Test
    fun getDatabase_returnNotFound() {
        webTestClient()
            .get()
            .uri("/api/database/$TEST_DB_NAME")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .logInfo()
            .expectStatus().isNotFound
            .expectHeader().value(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .expectBody()
            .logInfo()
            .jsonPath("$.databaseName").isEqualTo(TEST_DB_NAME)
            .jsonPath("$.collectionName").doesNotExist()
            .jsonPath("$.status").isEqualTo(404)
            .jsonPath("$.error").isEqualTo("Not found")
    }

    @Test
    fun addDatabase_returnOk() {
        webTestClient()
            .post()
            .uri("/api/database/$TEST_DB_NAME")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .logInfo()
            .expectStatus().isOk
            .expectHeader().value(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .expectBody()
            .logInfo()
            .jsonPath("$.name").isEqualTo(TEST_DB_NAME)
            .jsonPath("$.sizeOnDisk").isNumber
            .jsonPath("$.empty").isBoolean
            .consumeWith { mongoClient.assertDatabaseExists(TEST_DB_NAME) }
    }

    @Test
    fun deleteDatabase_returnNoContent() {
        mongoClient.createDatabase(TEST_DB_NAME)

        webTestClient()
            .delete()
            .uri("/api/database/$TEST_DB_NAME")
            .exchange()
            .logInfo()
            .expectStatus().isNoContent
            .expectBody()
            .jsonPath("$").doesNotExist()
            .consumeWith { mongoClient.assertDatabaseDoesNotExist(TEST_DB_NAME) }
    }

    @Test
    fun deleteDatabase_returnNotFound() {
        webTestClient()
            .delete()
            .uri("/api/database/$TEST_DB_NAME")
            .exchange()
            .logInfo()
            .expectStatus().isNotFound
            .expectBody()
            .jsonPath("$.databaseName").isEqualTo(TEST_DB_NAME)
            .jsonPath("$.collectionName").doesNotExist()
            .jsonPath("$.status").isEqualTo(404)
            .jsonPath("$.error").isEqualTo("Not found")
    }

    @Test
    fun getCollections_returnOk() {
        mongoClient.createDatabase(TEST_DB_NAME)
        mongoClient.createCollection(TEST_DB_NAME, TEST_COLLECTION_NAME)

        webTestClient()
            .get()
            .uri("/api/database/$TEST_DB_NAME/collection")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .logInfo()
            .expectStatus().isOk
            .expectHeader().value(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .expectBody()
            .logInfo()
            .jsonPath("$").isArray
            .jsonPath("$[0]").exists()
            .jsonPath("$[0].name").isEqualTo(TEST_COLLECTION_NAME)
            .jsonPath("$[1]").doesNotExist()
    }

    @Test
    fun getCollection_returnOk() {
        mongoClient.createDatabase(TEST_DB_NAME)
        mongoClient.createCollection(TEST_DB_NAME, TEST_COLLECTION_NAME)

        webTestClient()
            .get()
            .uri("/api/database/$TEST_DB_NAME/collection/$TEST_COLLECTION_NAME")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .logInfo()
            .expectStatus().isOk
            .expectHeader().value(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .expectBody()
            .logInfo()
            .jsonPath("$.name").isEqualTo(TEST_COLLECTION_NAME)
    }

    @Test
    fun getCollection_returnNotFound() {
        mongoClient.createDatabase(TEST_DB_NAME)

        webTestClient()
            .get()
            .uri("/api/database/$TEST_DB_NAME/collection/$TEST_COLLECTION_NAME")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .logInfo()
            .expectStatus().isNotFound
            .expectHeader().value(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .expectBody()
            .logInfo()
            .jsonPath("$.databaseName").isEqualTo(TEST_DB_NAME)
            .jsonPath("$.collectionName").isEqualTo(TEST_COLLECTION_NAME)
            .jsonPath("$.status").isEqualTo(404)
            .jsonPath("$.error").isEqualTo("Not found")
    }

    @Test
    fun getCollection_returnNotFoundDb() {
        webTestClient()
            .get()
            .uri("/api/database/$TEST_DB_NAME/collection/$TEST_COLLECTION_NAME")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .logInfo()
            .expectStatus().isNotFound
            .expectHeader().value(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .expectBody()
            .logInfo()
            .jsonPath("$.databaseName").isEqualTo(TEST_DB_NAME)
            .jsonPath("$.collectionName").doesNotExist()
            .jsonPath("$.status").isEqualTo(404)
            .jsonPath("$.error").isEqualTo("Not found")
    }

    @Test
    fun addCollection_returnOk() {
        mongoClient.createDatabase(TEST_DB_NAME)
        mongoClient.assertCollectionDoesNotExists(TEST_DB_NAME, TEST_COLLECTION_NAME)

        webTestClient()
            .post()
            .uri("/api/database/$TEST_DB_NAME/collection/$TEST_COLLECTION_NAME")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .logInfo()
            .expectStatus().isOk
            .expectHeader().value(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .expectBody()
            .logInfo()
            .jsonPath("$.name").isEqualTo(TEST_COLLECTION_NAME)
            .consumeWith { mongoClient.assertCollectionExists(TEST_DB_NAME, TEST_COLLECTION_NAME) }
    }

    @Test
    fun addCollection_returnNotFound() {

        webTestClient()
            .post()
            .uri("/api/database/$TEST_DB_NAME/collection/$TEST_COLLECTION_NAME")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .logInfo()
            .expectStatus().isNotFound
            .expectHeader().value(HttpHeaders.CONTENT_TYPE, equalTo(MediaType.APPLICATION_JSON_VALUE))
            .expectBody()
            .logInfo()
            .jsonPath("$.databaseName").isEqualTo(TEST_DB_NAME)
            .jsonPath("$.collectionName").doesNotExist()
            .jsonPath("$.status").isEqualTo(404)
            .jsonPath("$.error").isEqualTo("Not found")
    }

    @Test
    fun deleteCollection_returnNoContent() {
        mongoClient.createDatabase(TEST_DB_NAME)
        mongoClient.createCollection(TEST_DB_NAME, TEST_COLLECTION_NAME)

        webTestClient()
            .delete()
            .uri("/api/database/$TEST_DB_NAME/collection/$TEST_COLLECTION_NAME")
            .exchange()
            .logInfo()
            .expectStatus().isNoContent
            .expectBody()
            .logInfo()
            .jsonPath("$").doesNotExist()
            .consumeWith { mongoClient.assertCollectionDoesNotExists(TEST_DB_NAME, TEST_COLLECTION_NAME) }
    }

    @Test
    fun deleteCollection_returnNotFound() {
        mongoClient.createDatabase(TEST_DB_NAME)
        mongoClient.assertCollectionDoesNotExists(TEST_DB_NAME, TEST_COLLECTION_NAME)

        webTestClient()
            .delete()
            .uri("/api/database/$TEST_DB_NAME/collection/$TEST_COLLECTION_NAME")
            .exchange()
            .logInfo()
            .expectStatus().isNotFound
            .expectBody()
            .logInfo()
            .jsonPath("$.databaseName").isEqualTo(TEST_DB_NAME)
            .jsonPath("$.collectionName").isEqualTo(TEST_COLLECTION_NAME)
            .jsonPath("$.status").isEqualTo(404)
            .jsonPath("$.error").isEqualTo("Not found")
    }

    @Test
    fun deleteCollection_returnNotFoundDb() {
        webTestClient()
            .delete()
            .uri("/api/database/$TEST_DB_NAME/collection/$TEST_COLLECTION_NAME")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .logInfo()
            .expectStatus().isNotFound
            .expectBody()
            .logInfo()
            .jsonPath("$.databaseName").isEqualTo(TEST_DB_NAME)
            .jsonPath("$.collectionName").doesNotExist()
            .jsonPath("$.status").isEqualTo(404)
            .jsonPath("$.error").isEqualTo("Not found")
    }
}