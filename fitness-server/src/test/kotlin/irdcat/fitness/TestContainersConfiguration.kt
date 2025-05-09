package irdcat.fitness

import org.springframework.context.annotation.Configuration
import org.testcontainers.containers.MongoDBContainer

@Configuration
class TestContainersConfiguration {
    companion object {
        private val mongoDBContainer: MongoDBContainer = MongoDBContainer("mongo:latest").withExposedPorts(27017)

        init {
            mongoDBContainer.start()
            System.setProperty("mongodb.container.port", mongoDBContainer.getMappedPort(27017).toString())
        }
    }
}