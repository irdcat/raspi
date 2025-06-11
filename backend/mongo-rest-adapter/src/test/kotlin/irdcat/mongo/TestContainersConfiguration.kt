package irdcat.mongo

import org.springframework.context.annotation.Configuration
import org.testcontainers.containers.MongoDBContainer

@Configuration
@Suppress("unused")
class TestContainersConfiguration {
    companion object {
        private val mongoDBContainer = MongoDBContainer("mongo:latest").withExposedPorts(27017)
        private const val PORT_PROPERTY_KEY = "mongo.db.container.port"

        init {
            mongoDBContainer.start()
            mongoDBContainer
                .getMappedPort(27017).toString()
                .to(PORT_PROPERTY_KEY)
                .apply { System.setProperty(second, first) }
        }
    }
}