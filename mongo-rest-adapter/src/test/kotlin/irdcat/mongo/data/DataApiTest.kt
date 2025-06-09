package irdcat.mongo.data

import com.mongodb.reactivestreams.client.MongoClient
import irdcat.mongo.BaseApiTest
import org.bson.Document
import org.hamcrest.Matchers.equalTo
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.JsonPathAssertions
import org.springframework.test.web.reactive.server.WebTestClient.BodyContentSpec
import kotlin.test.BeforeTest
import kotlin.test.Test

internal class DataApiTest: BaseApiTest() {

    companion object {
        private const val TEST_DB_NAME = "spring-test"
        private const val TEST_COLLECTION_NAME = "collection-test"

        private const val ID_FIELD_NAME = "_id"
        private const val NAME_FIELD_NAME = "name"

        private const val TEST_ID = "testId"
        private const val TEST_NAME = "testName"

        private val TEST_DOCUMENT = Document(mapOf(
            ID_FIELD_NAME to TEST_ID,
            NAME_FIELD_NAME to TEST_NAME
        ))
    }

    fun JsonPathAssertions.assert(field: Any?) =
        if (field == null) { doesNotExist() } else { isEqualTo(field) }

    fun BodyContentSpec.validateDataApiError(dataApiError: DataApiError) = this
        .jsonPath("$.databaseName").assert(dataApiError.databaseName)
        .jsonPath("$.collectionName").assert(dataApiError.collectionName)
        .jsonPath("$.documentId").assert(dataApiError.documentId)
        .jsonPath("$.status").assert(dataApiError.status)
        .jsonPath("$.error").assert(dataApiError.error)


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
            .validateDataApiError(DataApiError(
                databaseName = TEST_DB_NAME,
                status = 404,
                error = "Not found"
            ))
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
            .logInfo()
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
            .logInfo()
            .validateDataApiError(DataApiError(
                databaseName = TEST_DB_NAME,
                status = 404,
                error = "Not found"
            ))
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
            .validateDataApiError(DataApiError(
                databaseName = TEST_DB_NAME,
                collectionName = TEST_COLLECTION_NAME,
                status = 404,
                error = "Not found"
            ))
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
            .validateDataApiError(DataApiError(
                databaseName = TEST_DB_NAME,
                status = 404,
                error = "Not found"
            ))
    }

    @Test
    fun addCollection_returnOk() {
        mongoClient.createDatabase(TEST_DB_NAME)
        mongoClient.assertCollectionDoesNotExist(TEST_DB_NAME, TEST_COLLECTION_NAME)

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
            .validateDataApiError(DataApiError(
                databaseName = TEST_DB_NAME,
                status = 404,
                error = "Not found"
            ))
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
            .consumeWith { mongoClient.assertCollectionDoesNotExist(TEST_DB_NAME, TEST_COLLECTION_NAME) }
    }

    @Test
    fun deleteCollection_returnNotFound() {
        mongoClient.createDatabase(TEST_DB_NAME)
        mongoClient.assertCollectionDoesNotExist(TEST_DB_NAME, TEST_COLLECTION_NAME)

        webTestClient()
            .delete()
            .uri("/api/database/$TEST_DB_NAME/collection/$TEST_COLLECTION_NAME")
            .exchange()
            .logInfo()
            .expectStatus().isNotFound
            .expectBody()
            .logInfo()
            .validateDataApiError(DataApiError(
                databaseName = TEST_DB_NAME,
                collectionName = TEST_COLLECTION_NAME,
                status = 404,
                error = "Not found"
            ))
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
            .validateDataApiError(DataApiError(
                databaseName = TEST_DB_NAME,
                status = 404,
                error = "Not found"
            ))
    }

    @Test
    fun getDocuments_returnOk() {
        mongoClient.createDatabase(TEST_DB_NAME)
        mongoClient.createCollection(TEST_DB_NAME, TEST_COLLECTION_NAME)
        mongoClient.addDocument(TEST_DB_NAME, TEST_COLLECTION_NAME, TEST_DOCUMENT)

        webTestClient()
            .get()
            .uri("/api/database/$TEST_DB_NAME/collection/$TEST_COLLECTION_NAME/document")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .logInfo()
            .expectStatus().isOk
            .expectBody()
            .logInfo()
            .jsonPath("$").isArray
            .jsonPath("$[0]").exists()
            .jsonPath("$[0].$ID_FIELD_NAME").isEqualTo(TEST_ID)
            .jsonPath("$[0].$NAME_FIELD_NAME").isEqualTo(TEST_NAME)
            .jsonPath("$[1]").doesNotExist()
    }

    @Test
    fun getDocuments_returnOk_empty() {
        mongoClient.createDatabase(TEST_DB_NAME)
        mongoClient.createCollection(TEST_DB_NAME, TEST_COLLECTION_NAME)

        webTestClient()
            .get()
            .uri("/api/database/$TEST_DB_NAME/collection/$TEST_COLLECTION_NAME/document")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .logInfo()
            .expectStatus().isOk
            .expectBody()
            .logInfo()
            .jsonPath("$").isArray
            .jsonPath("$[0]").doesNotExist()
    }

    @Test
    fun getDocument_returnOk() {
        mongoClient.createDatabase(TEST_DB_NAME)
        mongoClient.createCollection(TEST_DB_NAME, TEST_COLLECTION_NAME)
        mongoClient.addDocument(TEST_DB_NAME, TEST_COLLECTION_NAME, TEST_DOCUMENT)

        webTestClient()
            .get()
            .uri("/api/database/$TEST_DB_NAME/collection/$TEST_COLLECTION_NAME/document/$TEST_ID")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .logInfo()
            .expectStatus().isOk
            .expectBody()
            .logInfo()
            .jsonPath("$.$ID_FIELD_NAME").isEqualTo(TEST_ID)
            .jsonPath("$.$NAME_FIELD_NAME").isEqualTo(TEST_NAME)
    }

    @Test
    fun getDocument_returnNotFound() {
        mongoClient.createDatabase(TEST_DB_NAME)
        mongoClient.createCollection(TEST_DB_NAME, TEST_COLLECTION_NAME)

        webTestClient()
            .get()
            .uri("/api/database/$TEST_DB_NAME/collection/$TEST_COLLECTION_NAME/document/$TEST_ID")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .logInfo()
            .expectStatus().isNotFound
            .expectBody()
            .logInfo()
            .validateDataApiError(DataApiError(
                databaseName = TEST_DB_NAME,
                collectionName = TEST_COLLECTION_NAME,
                documentId = TEST_ID,
                status = 404,
                error = "Not found"
            ))
    }

    @Test
    fun getDocument_returnNotFoundCollection() {
        mongoClient.createDatabase(TEST_DB_NAME)

        webTestClient()
            .get()
            .uri("/api/database/$TEST_DB_NAME/collection/$TEST_COLLECTION_NAME/document/$TEST_ID")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .logInfo()
            .expectStatus().isNotFound
            .expectBody()
            .logInfo()
            .validateDataApiError(DataApiError(
                databaseName = TEST_DB_NAME,
                collectionName = TEST_COLLECTION_NAME,
                status = 404,
                error = "Not found"
            ))
    }

    @Test
    fun getDocument_returnNotFoundDb() {
        webTestClient()
            .get()
            .uri("/api/database/$TEST_DB_NAME/collection/$TEST_COLLECTION_NAME/document/$TEST_ID")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .logInfo()
            .expectStatus().isNotFound
            .expectBody()
            .logInfo()
            .validateDataApiError(DataApiError(
                databaseName = TEST_DB_NAME,
                status = 404,
                error = "Not found"
            ))
    }
}