package irdcat.fitness

import irdcat.fitness.model.*
import irdcat.fitness.repository.ExerciseRepository
import irdcat.fitness.repository.ExerciseTypeRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.format.datetime.DateFormatter
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.Instant
import java.util.*

@AutoConfigureWebTestClient
class ExerciseApiTests : AbstractIntegrationTest() {

    private val dateNow = Date.from(Instant.now())
    private val dateNowString = DateFormatter("yyyy-MM-dd").print(dateNow, Locale.GERMAN)

    private val exercises = listOf(
        Exercise(
            "0",
            "1",
            dateNow,
            0,
            60.0f,
            listOf(
                ExerciseSet(10, 30.0f),
                ExerciseSet(10, 30.0f)
            )
        ),
        Exercise(
            "1",
            "2",
            dateNow,
            1,
            60.0f,
            listOf(
                ExerciseSet(10, 30.0f),
                ExerciseSet(10, 30.0f)
            )
        )
    )

    private val exerciseTypes = listOf(
        ExerciseType("1", "Bench Press", false),
        ExerciseType("2", "Pull up", true)
    )

    @Autowired
    private lateinit var exerciseRepository: ExerciseRepository

    @Autowired
    private lateinit var exerciseTypeRepository: ExerciseTypeRepository

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @BeforeEach
    fun beforeEach() {
        exerciseRepository.deleteAll().block()
        exerciseTypeRepository.deleteAll().block()
    }

    @Test
    fun getAllExercises_ok() {
        exerciseRepository.insert(exercises).blockLast()

        webTestClient
            .get()
            .uri("/api/exercises")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.[0].id").isEqualTo("0")
            .jsonPath("$.[0].typeId").isEqualTo("1")
            .jsonPath("$.[0].date").isEqualTo(dateNowString)
            .jsonPath("$.[0].order").isEqualTo(0)
            .jsonPath("$.[0].bodyWeight").isEqualTo(60.0f)
            .jsonPath("$.[0].sets[0].repetitions").isEqualTo(10)
            .jsonPath("$.[0].sets[0].weight").isEqualTo(30.0f)
            .jsonPath("$.[0].sets[1].repetitions").isEqualTo(10)
            .jsonPath("$.[0].sets[1].weight").isEqualTo(30.0f)
            .jsonPath("$.[1].id").isEqualTo("1")
            .jsonPath("$.[1].typeId").isEqualTo("2")
            .jsonPath("$.[1].date").isEqualTo(dateNowString)
            .jsonPath("$.[1].order").isEqualTo(1)
            .jsonPath("$.[1].bodyWeight").isEqualTo(60.0f)
            .jsonPath("$.[1].sets[0].repetitions").isEqualTo(10)
            .jsonPath("$.[1].sets[0].weight").isEqualTo(30.0f)
            .jsonPath("$.[1].sets[1].repetitions").isEqualTo(10)
            .jsonPath("$.[1].sets[1].weight").isEqualTo(30.0f)
    }

    @Test
    fun getAllExercises_notFound() {
        webTestClient
            .get()
            .uri("/api/exercises")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectStatus().isNotFound
    }

    @Test
    fun getExercise_ok() {
        exerciseRepository.insert(exercises[0]).block()

        webTestClient
            .get()
            .uri("/api/exercises/0")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo("0")
            .jsonPath("$.typeId").isEqualTo("1")
            .jsonPath("$.date").isEqualTo(dateNowString)
            .jsonPath("$.order").isEqualTo(0)
            .jsonPath("$.bodyWeight").isEqualTo(60.0f)
            .jsonPath("$.sets[0].repetitions").isEqualTo(10)
            .jsonPath("$.sets[0].weight").isEqualTo(30.0f)
            .jsonPath("$.sets[1].repetitions").isEqualTo(10)
            .jsonPath("$.sets[1].weight").isEqualTo(30.0f)
    }

    @Test
    fun getExercise_notFound() {
        webTestClient
            .get()
            .uri("/api/exercises/0")
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
            .bodyValue(ExerciseDTO.fromExercise(exercises[0]))
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo("0")
            .jsonPath("$.typeId").isEqualTo("1")
            .jsonPath("$.date").isEqualTo(dateNowString)
            .jsonPath("$.order").isEqualTo(0)
            .jsonPath("$.bodyWeight").isEqualTo(60.0f)
            .jsonPath("$.sets[0].repetitions").isEqualTo(10)
            .jsonPath("$.sets[0].weight").isEqualTo(30.0f)
            .jsonPath("$.sets[1].repetitions").isEqualTo(10)
            .jsonPath("$.sets[1].weight").isEqualTo(30.0f)
    }

    @Test
    fun getExerciseTypes_ok() {
        exerciseTypeRepository.insert(exerciseTypes).blockLast()

        webTestClient
            .get()
            .uri("/api/exercises/types")
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
    fun getExerciseTypes_notFound() {
        webTestClient
            .get()
            .uri("/api/exercises/types")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectStatus().isNotFound
    }

    @Test
    fun getExerciseType_ok() {
        exerciseTypeRepository.insert(exerciseTypes[0]).block()

        webTestClient
            .get()
            .uri("/api/exercises/types/1")
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
    fun getExerciseType_notFound() {
        webTestClient
            .get()
            .uri("/api/exercises/types/1")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectStatus().isNotFound
    }

    @Test
    fun addExerciseType_ok() {
        webTestClient
            .post()
            .uri("/api/exercises/types")
            .bodyValue(ExerciseTypeDTO.fromExerciseType(exerciseTypes[0]))
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo("1")
            .jsonPath("$.name").isEqualTo("Bench Press")
            .jsonPath("$.isBodyWeight").isEqualTo(false)
    }
}