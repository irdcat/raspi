package irdcat.fitness.api

import irdcat.fitness.AbstractIntegrationTest
import irdcat.fitness.service.TrainingExercise
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Query
import org.springframework.test.web.reactive.server.WebTestClient
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
            .map { logger.info("Added {}", it) }
            .blockLast()
    }

    protected fun cleanupTrainingExercises() {
        reactiveMongoTemplate
            .remove(Query(), TrainingExercise::class.java)
            .block()
    }

    protected fun String.toLocalDate(): LocalDate = LocalDate.parse(this)
}
