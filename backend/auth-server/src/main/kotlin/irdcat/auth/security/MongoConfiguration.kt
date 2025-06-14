package irdcat.auth.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.mongodb.MongoDatabaseFactory
import org.springframework.data.mongodb.core.convert.DefaultDbRefResolver
import org.springframework.data.mongodb.core.convert.MappingMongoConverter
import org.springframework.data.mongodb.core.convert.MongoConverter
import org.springframework.data.mongodb.core.convert.MongoCustomConversions
import org.springframework.data.mongodb.core.mapping.MongoMappingContext

@Configuration
class MongoConfiguration {

    @Bean
    fun mongoConverter(
        databaseFactory: MongoDatabaseFactory,
        mappingContext: MongoMappingContext,
        customConversions: MongoCustomConversions
    ): MongoConverter {
        val dbRefResolver = DefaultDbRefResolver(databaseFactory);
        val converter = MappingMongoConverter(dbRefResolver, mappingContext);
        converter.setCustomConversions(customConversions)
        converter.setCodecRegistryProvider(databaseFactory)
        converter.setMapKeyDotReplacement("#")
        return converter
    }
}