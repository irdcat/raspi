package irdcat.fitness.api

import irdcat.fitness.AbstractIntegrationTest
import irdcat.fitness.model.*
import irdcat.fitness.repository.ExerciseRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

@AutoConfigureWebTestClient
class ExerciseApiTests : AbstractIntegrationTest() {

    private val exercises = listOf(
        Exercise("1", "Bench Press", false),
        Exercise("2", "Pull up", true)
    )

    @Autowired
    private lateinit var exerciseRepository: ExerciseRepository

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @BeforeEach
    fun beforeEach() {
        exerciseRepository.deleteAll().block()
    }

    @Test
    fun getExercises_ok() {
        exerciseRepository.insert(exercises).blockLast()

        webTestClient
            .get()
            .uri("/api/exercises")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.[0].id").isEqualTo("1")
            .jsonPath("$.[0].name").isEqualTo("Bench Press")
            .jsonPath("$.[0].isBodyWeight").isEqualTo(false)
            .jsonPath("$.[1].id").isEqualTo("2")
            .jsonPath("$.[1].name").isEqualTo("Pull up")
            .jsonPath("$.[1].isBodyWeight").isEqualTo(true)
    }

    @Test
    fun getExercises_notFound() {
        webTestClient
            .get()
            .uri("/api/exercises")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$").isArray
            .jsonPath("$.[0]").doesNotExist()
    }

    @Test
    fun getExercise_ok() {
        exerciseRepository.insert(exercises[0]).block()

        webTestClient
            .get()
            .uri("/api/exercises/1")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo("1")
            .jsonPath("$.name").isEqualTo("Bench Press")
            .jsonPath("$.isBodyWeight").isEqualTo(false)
    }

    @Test
    fun getExercise_notFound() {
        webTestClient
            .get()
            .uri("/api/exercises/1")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectStatus().isNotFound
    }

    @Test
    fun addExercise_ok() {
        webTestClient
            .post()
            .uri("/api/exercises")
            .bodyValue(ExerciseDto.fromExercise(exercises[0]))
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo("1")
            .jsonPath("$.name").isEqualTo("Bench Press")
            .jsonPath("$.isBodyWeight").isEqualTo(false)
    }

    @Test
    fun updateExercise_ok() {
        exerciseRepository.insert(exercises[0]).block()

        webTestClient
            .put()
            .uri("/api/exercises/1")
            .bodyValue(ExerciseDto.fromExercise(exercises[0]).copy(id = null, name = "Dip", isBodyWeight = true))
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo("1")
            .jsonPath("$.name").isEqualTo("Dip")
            .jsonPath("$.isBodyWeight").isEqualTo(true)
    }

    @Test
    fun updateExercise_badRequest() {
        exerciseRepository.insert(exercises[0]).block()

        webTestClient
            .put()
            .uri("/api/exercises/1")
            .bodyValue(ExerciseDto.fromExercise(exercises[0]).copy(id = null, name = null, isBodyWeight = true))
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectStatus().isBadRequest
    }

    @Test
    fun deleteExercise_ok() {
        exerciseRepository.insert(exercises[0]).block()

        webTestClient
            .delete()
            .uri("/api/exercises/1")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo("1")
            .jsonPath("$.name").doesNotExist()
            .jsonPath("$.isBodyWeight").doesNotExist()
    }
}