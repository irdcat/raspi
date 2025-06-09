package irdcat.mongo.data

import kotlin.test.Test
import kotlin.test.assertEquals

class ExceptionsTest {

    @Test
    fun testDatabaseNotFoundException() {
        val exception = DatabaseNotFoundException("test-db")

        assertEquals("Not found: test-db", exception.message)
    }

    @Test
    fun testCollectionNotFoundException() {
        val exception = CollectionNotFoundException("test-db", "test-collection")

        assertEquals("Not found: test-collection in test-db", exception.message)
    }

    @Test
    fun testDocumentNotFoundException() {
        val exception = DocumentNotFoundException("test-db", "test-collection", "test-document")

        assertEquals("Not found: test-document in test-collection in test-db", exception.message)
    }
}