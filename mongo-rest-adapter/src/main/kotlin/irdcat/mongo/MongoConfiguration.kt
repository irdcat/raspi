package irdcat.mongo

import com.mongodb.reactivestreams.client.MongoClient
import com.mongodb.reactivestreams.client.MongoClients
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@EnableConfigurationProperties(MongoProperties::class)
class MongoConfiguration {

    @Bean
    fun mongoClient(mongoProperties: MongoProperties): MongoClient {
        return mongoProperties
            .toConnectionString()
            .let(MongoClients::create)
    }

}