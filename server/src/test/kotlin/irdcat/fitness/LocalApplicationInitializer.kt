package irdcat.fitness

import irdcat.fitness.model.Exercise
import irdcat.fitness.repository.ExerciseRepository
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.InitializingBean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import org.yaml.snakeyaml.Yaml
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@Profile("!test")
@Configuration
class LocalApplicationInitializer(
    private val exerciseRepository: ExerciseRepository
): InitializingBean {

    companion object {
        @JvmStatic
        private val logger = LoggerFactory.getLogger(LocalApplicationInitializer::class.java)

        private const val TEST_EXERCISES_FILE = "test-exercises.yaml"
    }

    override fun afterPropertiesSet() {
        Mono.just(TEST_EXERCISES_FILE)
            .map { this.javaClass.classLoader.getResourceAsStream(it) }
            .map { Yaml().load<List<LinkedHashMap<String, Any>>>(it) }
            .flatMapMany { Flux.fromIterable(it!!) }
            .map {
                Exercise(
                    it["id"]!!.toString(),
                    it["name"]!!.toString(),
                    it["isBodyWeight"]!!.toString().toBoolean()
                )
            }
            .collectList()
            .flatMapMany { exerciseRepository.insert(it) }
            .subscribe { logger.info("Added {} into database", it) }
    }
}