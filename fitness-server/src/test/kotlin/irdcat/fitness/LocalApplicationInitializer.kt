package irdcat.fitness

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import irdcat.fitness.service.TrainingExercise
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import reactor.kotlin.core.publisher.toMono

@Profile("!test")
@Configuration
class LocalApplicationInitializer(
    private val reactiveMongoTemplate: ReactiveMongoTemplate
): InitializingBean {

    companion object {
        @JvmStatic
        private val logger = LoggerFactory.getLogger(LocalApplicationInitializer::class.java)

        @JvmStatic
        private val yaml = ObjectMapper(YAMLFactory()).registerKotlinModule()

        private const val TEST_DATA_FILE_NAME = "test-data.yaml"
    }

    override fun afterPropertiesSet() {
        TEST_DATA_FILE_NAME.toMono()
            .map { this.javaClass.classLoader.getResourceAsStream(it) }
            .map { yaml.readValue(it, object: TypeReference<List<TrainingExercise>>() {}) }
            .flatMapMany { reactiveMongoTemplate.insert(it, TrainingExercise::class.java) }
            .subscribe { logger.info("Added exercise {}", it) }
    }
}