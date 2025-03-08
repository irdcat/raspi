package irdcat.fitness.api

import irdcat.fitness.AbstractIntegrationTest
import irdcat.fitness.service.TrainingExercise
import irdcat.fitness.service.TrainingTemplate
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.test.StepVerifier
import java.text.SimpleDateFormat
import java.time.LocalDate

@AutoConfigureWebTestClient
abstract class AbstractApiTest: AbstractIntegrationTest() {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @Autowired
    private lateinit var reactiveMongoTemplate: ReactiveMongoTemplate

    protected fun webTestClient(): WebTestClient = webTestClient

    protected fun insertTrainingExercises(trainingExercises: List<TrainingExercise>) {
        reactiveMongoTemplate
            .insert(trainingExercises, TrainingExercise::class.java)
            .map { logger.info("Added TrainingExercise: {}", it) }
            .blockLast()
    }

    protected fun cleanupTrainingExercises() {
        reactiveMongoTemplate
            .remove(Query(), TrainingExercise::class.java)
            .block()
    }

    protected fun insertTrainingTemplates(trainingTemplates: List<TrainingTemplate>) {
        reactiveMongoTemplate
            .insert(trainingTemplates, TrainingTemplate::class.java)
            .map { logger.info("Added TrainingTemplate: {}", it) }
            .blockLast()
    }

    protected fun cleanupTrainingTemplates() {
        reactiveMongoTemplate
            .remove(Query(), TrainingTemplate::class.java)
            .block()
    }

    protected fun <T> assertCollectionIsEmpty(type: Class<T>) {
        val count = reactiveMongoTemplate
            .count(Query(), type)
        StepVerifier.create(count)
            .expectNext(0)
    }

    protected fun String.toLocalDate(): LocalDate = LocalDate.parse(this)
}
