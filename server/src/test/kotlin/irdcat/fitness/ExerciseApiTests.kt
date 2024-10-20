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
import java.time.Duration
import java.time.Instant
import java.util.*

@AutoConfigureWebTestClient
class ExerciseApiTests : AbstractIntegrationTest() {

    private val dateFormatter = DateFormatter("yyyy-MM-dd")

    private val dateNow = Date.from(Instant.EPOCH.plus(Duration.ofDays(30)))
    private val dateNowString = dateFormatter.print(dateNow, Locale.GERMAN)

    private val dateFrom = Date.from(Instant.EPOCH)

    private val dateTo = Date.from(Instant.EPOCH.plus(Duration.ofDays(60)))

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
    fun filterExercises_ok() {
        exerciseRepository
            .insert(exercises)
            .blockLast()

        webTestClient
            .post()
            .uri("/api/exercises/filter")
            .bodyValue(ExerciseFilterDto())
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
    fun filterExercises_byTypeId_ok() {
        exerciseRepository
            .insert(exercises)
            .blockLast()

        webTestClient
            .post()
            .uri("/api/exercises/filter")
            .bodyValue(ExerciseFilterDto(typeId = "1"))
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
    }

    @Test
    fun filterExercises_betweenDates_ok() {
        exerciseRepository
            .insert(exercises)
            .blockLast()

        webTestClient
            .post()
            .uri("/api/exercises/filter")
            .bodyValue(ExerciseFilterDto(from = dateFrom, to = dateTo))
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
    fun filterExercises_fromDate_ok() {
        exerciseRepository
            .insert(exercises)
            .blockLast()

        webTestClient
            .post()
            .uri("/api/exercises/filter")
            .bodyValue(ExerciseFilterDto(from = dateFrom))
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
    fun filterExercises_betweenDatesByTypeId() {
        exerciseRepository
            .insert(exercises)
            .blockLast()

        webTestClient
            .post()
            .uri("/api/exercises/filter")
            .bodyValue(ExerciseFilterDto(typeId = "1", from = dateFrom, to = dateTo))
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
    }

    @Test
    fun filterExercises_fromDateByTypeId() {
        exerciseRepository
            .insert(exercises)
            .blockLast()

        webTestClient
            .post()
            .uri("/api/exercises/filter")
            .bodyValue(ExerciseFilterDto(from = dateFrom, typeId = "1"))
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
    }

    @Test
    fun addExercise_ok() {
        webTestClient
            .post()
            .uri("/api/exercises")
            .bodyValue(ExerciseDto.fromExercise(exercises[0]).copy(id = ""))
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isNotEmpty
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
    fun updateExercise_ok() {
        exerciseRepository
            .insert(exercises)
            .blockLast()

        webTestClient
            .put()
            .uri("/api/exercises")
            .bodyValue(ExerciseDto.fromExercise(exercises[0]).copy(order = 3))
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo("0")
            .jsonPath("$.typeId").isEqualTo("1")
            .jsonPath("$.date").isEqualTo(dateNowString)
            .jsonPath("$.order").isEqualTo(3)
            .jsonPath("$.bodyWeight").isEqualTo(60.0f)
            .jsonPath("$.sets[0].repetitions").isEqualTo(10)
            .jsonPath("$.sets[0].weight").isEqualTo(30.0f)
            .jsonPath("$.sets[1].repetitions").isEqualTo(10)
            .jsonPath("$.sets[1].weight").isEqualTo(30.0f)
    }

    @Test
    fun updateExercise_badRequest() {
        exerciseRepository
            .insert(exercises)
            .blockLast()

        webTestClient
            .put()
            .uri("/api/exercises")
            .bodyValue(ExerciseDto.fromExercise(exercises[0]).copy(id = "", order = 3))
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectStatus().isBadRequest
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
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$").isArray
            .jsonPath("$.[0]").doesNotExist()
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
            .bodyValue(ExerciseTypeDto.fromExerciseType(exerciseTypes[0]))
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
    fun updateExerciseType_ok() {
        exerciseTypeRepository.insert(exerciseTypes[0]).block()

        webTestClient
            .put()
            .uri("/api/exercises/types")
            .bodyValue(ExerciseTypeDto.fromExerciseType(exerciseTypes[0]).copy(name = "Dip", isBodyWeight = true))
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
    fun updateExerciseType_badRequest() {
        exerciseTypeRepository.insert(exerciseTypes[0]).block()

        webTestClient
            .put()
            .uri("/api/exercises/types")
            .bodyValue(ExerciseTypeDto.fromExerciseType(exerciseTypes[0]).copy(id = "", name = "Dip", isBodyWeight = true))
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectStatus().isBadRequest
    }
}