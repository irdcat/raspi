package irdcat.fitness.api

import irdcat.fitness.AbstractIntegrationTest
import irdcat.fitness.model.*
import irdcat.fitness.repository.TrainingRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@AutoConfigureWebTestClient
class TrainingApiTests: AbstractIntegrationTest() {

    private val trainings = listOf(
        Training(
            "1",
            LocalDate.now().minusDays(1),
            60.0f,
            listOf(
                TrainingExercise(
                    0,
                    "1",
                    listOf(
                        TrainingExerciseSet(10, 30.0f),
                        TrainingExerciseSet(10, 30.0f),
                        TrainingExerciseSet(10, 30.0f)
                    )),
                TrainingExercise(
                    1,
                    "2",
                    listOf(
                        TrainingExerciseSet(10, 30.0f),
                        TrainingExerciseSet(10, 30.0f),
                        TrainingExerciseSet(10, 30.0f)
                    ))
            )),
        Training(
            "2",
            LocalDate.now().minusDays(2),
            60.0f,
            listOf(
                TrainingExercise(
                    0,
                    "1",
                    listOf(
                        TrainingExerciseSet(10, 40.0f),
                        TrainingExerciseSet(10, 40.0f),
                        TrainingExerciseSet(10, 40.0f)
                    )),
                TrainingExercise(
                    1,
                    "2",
                    listOf(
                        TrainingExerciseSet(10, 40.0f),
                        TrainingExerciseSet(10, 40.0f),
                        TrainingExerciseSet(10, 40.0f)
                    ))
            )),
    )

    val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
    val formattedDates = trainings.map { dateFormatter.format(it.date) }

    @Autowired
    private lateinit var trainingRepository: TrainingRepository

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @BeforeEach
    fun beforeEach() {
        trainingRepository.deleteAll().block()
    }

    @Test
    fun getTrainings_ok() {
        trainingRepository.insert(trainings).blockLast()

        webTestClient
            .get()
            .uri("/api/trainings")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.[0].id").isEqualTo("1")
            .jsonPath("$.[0].date").isNotEmpty
            .jsonPath("$.[0].bodyWeight").isEqualTo(60.0f)
            .jsonPath("$.[0].exercises[0].order").isEqualTo(0)
            .jsonPath("$.[0].exercises[0].exerciseId").isEqualTo("1")
            .jsonPath("$.[0].exercises[0].sets[0].reps").isEqualTo(10)
            .jsonPath("$.[0].exercises[0].sets[0].weight").isEqualTo(30.0f)
            .jsonPath("$.[0].exercises[0].sets[1].reps").isEqualTo(10)
            .jsonPath("$.[0].exercises[0].sets[1].weight").isEqualTo(30.0f)
            .jsonPath("$.[0].exercises[0].sets[2].reps").isEqualTo(10)
            .jsonPath("$.[0].exercises[0].sets[2].weight").isEqualTo(30.0f)
            .jsonPath("$.[0].exercises[1].order").isEqualTo(1)
            .jsonPath("$.[0].exercises[1].exerciseId").isEqualTo("2")
            .jsonPath("$.[0].exercises[1].sets[0].reps").isEqualTo(10)
            .jsonPath("$.[0].exercises[1].sets[0].weight").isEqualTo(30.0f)
            .jsonPath("$.[0].exercises[1].sets[1].reps").isEqualTo(10)
            .jsonPath("$.[0].exercises[1].sets[1].weight").isEqualTo(30.0f)
            .jsonPath("$.[0].exercises[1].sets[2].reps").isEqualTo(10)
            .jsonPath("$.[0].exercises[1].sets[2].weight").isEqualTo(30.0f)
            .jsonPath("$.[1].id").isEqualTo("2")
            .jsonPath("$.[1].date").isNotEmpty
            .jsonPath("$.[1].bodyWeight").isEqualTo(60.0f)
            .jsonPath("$.[1].exercises[0].order").isEqualTo(0)
            .jsonPath("$.[1].exercises[0].exerciseId").isEqualTo("1")
            .jsonPath("$.[1].exercises[0].sets[0].reps").isEqualTo(10)
            .jsonPath("$.[1].exercises[0].sets[0].weight").isEqualTo(40.0f)
            .jsonPath("$.[1].exercises[0].sets[1].reps").isEqualTo(10)
            .jsonPath("$.[1].exercises[0].sets[1].weight").isEqualTo(40.0f)
            .jsonPath("$.[1].exercises[0].sets[2].reps").isEqualTo(10)
            .jsonPath("$.[1].exercises[0].sets[2].weight").isEqualTo(40.0f)
            .jsonPath("$.[1].exercises[1].order").isEqualTo(1)
            .jsonPath("$.[1].exercises[1].exerciseId").isEqualTo("2")
            .jsonPath("$.[1].exercises[1].sets[0].reps").isEqualTo(10)
            .jsonPath("$.[1].exercises[1].sets[0].weight").isEqualTo(40.0f)
            .jsonPath("$.[1].exercises[1].sets[1].reps").isEqualTo(10)
            .jsonPath("$.[1].exercises[1].sets[1].weight").isEqualTo(40.0f)
            .jsonPath("$.[1].exercises[1].sets[2].reps").isEqualTo(10)
            .jsonPath("$.[1].exercises[1].sets[2].weight").isEqualTo(40.0f)
    }

    @Test
    fun getTrainings_notFound() {
        webTestClient
            .get()
            .uri("/api/trainings")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$").isArray
            .jsonPath("$.[0]").doesNotExist()
    }

    @Test
    fun getTraining_ok() {
        trainingRepository.insert(trainings).blockLast()

        webTestClient
            .get()
            .uri("/api/trainings/1")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo("1")
            .jsonPath("$.date").isNotEmpty
            .jsonPath("$.bodyWeight").isEqualTo(60.0f)
            .jsonPath("$.exercises[0].order").isEqualTo(0)
            .jsonPath("$.exercises[0].exerciseId").isEqualTo("1")
            .jsonPath("$.exercises[0].sets[0].reps").isEqualTo(10)
            .jsonPath("$.exercises[0].sets[0].weight").isEqualTo(30.0f)
            .jsonPath("$.exercises[0].sets[1].reps").isEqualTo(10)
            .jsonPath("$.exercises[0].sets[1].weight").isEqualTo(30.0f)
            .jsonPath("$.exercises[0].sets[2].reps").isEqualTo(10)
            .jsonPath("$.exercises[0].sets[2].weight").isEqualTo(30.0f)
            .jsonPath("$.exercises[1].order").isEqualTo(1)
            .jsonPath("$.exercises[1].exerciseId").isEqualTo("2")
            .jsonPath("$.exercises[1].sets[0].reps").isEqualTo(10)
            .jsonPath("$.exercises[1].sets[0].weight").isEqualTo(30.0f)
            .jsonPath("$.exercises[1].sets[1].reps").isEqualTo(10)
            .jsonPath("$.exercises[1].sets[1].weight").isEqualTo(30.0f)
            .jsonPath("$.exercises[1].sets[2].reps").isEqualTo(10)
            .jsonPath("$.exercises[1].sets[2].weight").isEqualTo(30.0f)
    }

    @Test
    fun getTraining_notFound() {
        webTestClient
            .get()
            .uri("/api/trainings/1")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectStatus().isNotFound
    }

    @Test
    fun addTraining_ok() {
        webTestClient
            .post()
            .uri("/api/trainings")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TrainingDto.fromTraining(trainings[0]))
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo("1")
            .jsonPath("$.date").isNotEmpty
            .jsonPath("$.bodyWeight").isEqualTo(60.0f)
            .jsonPath("$.exercises[0].order").isEqualTo(0)
            .jsonPath("$.exercises[0].exerciseId").isEqualTo("1")
            .jsonPath("$.exercises[0].sets[0].reps").isEqualTo(10)
            .jsonPath("$.exercises[0].sets[0].weight").isEqualTo(30.0f)
            .jsonPath("$.exercises[0].sets[1].reps").isEqualTo(10)
            .jsonPath("$.exercises[0].sets[1].weight").isEqualTo(30.0f)
            .jsonPath("$.exercises[0].sets[2].reps").isEqualTo(10)
            .jsonPath("$.exercises[0].sets[2].weight").isEqualTo(30.0f)
            .jsonPath("$.exercises[1].order").isEqualTo(1)
            .jsonPath("$.exercises[1].exerciseId").isEqualTo("2")
            .jsonPath("$.exercises[1].sets[0].reps").isEqualTo(10)
            .jsonPath("$.exercises[1].sets[0].weight").isEqualTo(30.0f)
            .jsonPath("$.exercises[1].sets[1].reps").isEqualTo(10)
            .jsonPath("$.exercises[1].sets[1].weight").isEqualTo(30.0f)
            .jsonPath("$.exercises[1].sets[2].reps").isEqualTo(10)
            .jsonPath("$.exercises[1].sets[2].weight").isEqualTo(30.0f)
    }

    @Test
    fun updateTraining_ok() {
        trainingRepository.insert(trainings[0]).block()

        webTestClient
            .put()
            .uri("/api/trainings/1")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(TrainingDto.fromTraining(trainings[0].copy(bodyWeight = 74.0f)))
            .exchange()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo("1")
            .jsonPath("$.date").isNotEmpty
            .jsonPath("$.bodyWeight").isEqualTo(74.0f)
            .jsonPath("$.exercises[0].order").isEqualTo(0)
            .jsonPath("$.exercises[0].exerciseId").isEqualTo("1")
            .jsonPath("$.exercises[0].sets[0].reps").isEqualTo(10)
            .jsonPath("$.exercises[0].sets[0].weight").isEqualTo(30.0f)
            .jsonPath("$.exercises[0].sets[1].reps").isEqualTo(10)
            .jsonPath("$.exercises[0].sets[1].weight").isEqualTo(30.0f)
            .jsonPath("$.exercises[0].sets[2].reps").isEqualTo(10)
            .jsonPath("$.exercises[0].sets[2].weight").isEqualTo(30.0f)
            .jsonPath("$.exercises[1].order").isEqualTo(1)
            .jsonPath("$.exercises[1].exerciseId").isEqualTo("2")
            .jsonPath("$.exercises[1].sets[0].reps").isEqualTo(10)
            .jsonPath("$.exercises[1].sets[0].weight").isEqualTo(30.0f)
            .jsonPath("$.exercises[1].sets[1].reps").isEqualTo(10)
            .jsonPath("$.exercises[1].sets[1].weight").isEqualTo(30.0f)
            .jsonPath("$.exercises[1].sets[2].reps").isEqualTo(10)
            .jsonPath("$.exercises[1].sets[2].weight").isEqualTo(30.0f)
    }

    @Test
    fun updateTraining_badRequest() {
        trainingRepository.insert(trainings[0]).block()

        webTestClient
            .put()
            .uri("/api/trainings/1")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(TrainingDto.fromTraining(trainings[0]).copy(bodyWeight = null))
            .exchange()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectStatus().isBadRequest
    }

    @Test
    fun deleteTraining_ok() {
        trainingRepository.insert(trainings[0]).block()

        webTestClient
            .delete()
            .uri("/api/trainings/1")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo("1")
            .jsonPath("$.date").doesNotExist()
    }

    @Test
    fun trainingSummaries_ok() {
        trainingRepository.insert(trainings).blockLast()

        webTestClient
            .post()
            .uri("/api/trainings/summary")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(TrainingSummaryParamsDto(
                listOf("1"),
                LocalDate.now().minusDays(30),
                LocalDate.now()))
            .exchange()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$").isArray
            .jsonPath("$.[0].id").isEqualTo("1")
            .jsonPath("$.[0].parameters.%s.volume", formattedDates[0]).isEqualTo(900)
            .jsonPath("$.[0].parameters.%s.averageVolume", formattedDates[0]).isEqualTo(300)
            .jsonPath("$.[0].parameters.%s.averageIntensity", formattedDates[0]).isEqualTo(30)
            .jsonPath("$.[0].parameters.%s.minIntensity", formattedDates[0]).isEqualTo(30)
            .jsonPath("$.[0].parameters.%s.maxIntensity", formattedDates[0]).isEqualTo(30)
            .jsonPath("$.[0].parameters.%s.volume", formattedDates[1]).isEqualTo(1200)
            .jsonPath("$.[0].parameters.%s.averageVolume", formattedDates[1]).isEqualTo(400)
            .jsonPath("$.[0].parameters.%s.averageIntensity", formattedDates[1]).isEqualTo(40)
            .jsonPath("$.[0].parameters.%s.minIntensity", formattedDates[1]).isEqualTo(40)
            .jsonPath("$.[0].parameters.%s.maxIntensity", formattedDates[1]).isEqualTo(40)
            .jsonPath("$.[1].id").isEqualTo("2")
            .jsonPath("$.[1].parameters.%s.volume", formattedDates[0]).isEqualTo(900)
            .jsonPath("$.[1].parameters.%s.averageVolume", formattedDates[0]).isEqualTo(300)
            .jsonPath("$.[1].parameters.%s.averageIntensity", formattedDates[0]).isEqualTo(30)
            .jsonPath("$.[1].parameters.%s.minIntensity", formattedDates[0]).isEqualTo(30)
            .jsonPath("$.[1].parameters.%s.maxIntensity", formattedDates[0]).isEqualTo(30)
            .jsonPath("$.[1].parameters.%s.volume", formattedDates[1]).isEqualTo(1200)
            .jsonPath("$.[1].parameters.%s.averageVolume", formattedDates[1]).isEqualTo(400)
            .jsonPath("$.[1].parameters.%s.averageIntensity", formattedDates[1]).isEqualTo(40)
            .jsonPath("$.[1].parameters.%s.minIntensity", formattedDates[1]).isEqualTo(40)
            .jsonPath("$.[1].parameters.%s.maxIntensity", formattedDates[1]).isEqualTo(40)
    }

    @Test
    fun trainingSummary_ok() {
        trainingRepository.insert(trainings).blockLast()

        webTestClient
            .post()
            .uri("/api/trainings/summary")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(TrainingSummaryParamsDto(
                listOf("1"),
                LocalDate.now().minusDays(30),
                LocalDate.now()))
            .exchange()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$").isArray
            .jsonPath("$.[0].id").isEqualTo("1")
            .jsonPath("$.[0].parameters.%s.volume", formattedDates[0]).isEqualTo(900)
            .jsonPath("$.[0].parameters.%s.averageVolume", formattedDates[0]).isEqualTo(300)
            .jsonPath("$.[0].parameters.%s.averageIntensity", formattedDates[0]).isEqualTo(30)
            .jsonPath("$.[0].parameters.%s.minIntensity", formattedDates[0]).isEqualTo(30)
            .jsonPath("$.[0].parameters.%s.maxIntensity", formattedDates[0]).isEqualTo(30)
            .jsonPath("$.[0].parameters.%s.volume", formattedDates[1]).isEqualTo(1200)
            .jsonPath("$.[0].parameters.%s.averageVolume", formattedDates[1]).isEqualTo(400)
            .jsonPath("$.[0].parameters.%s.averageIntensity", formattedDates[1]).isEqualTo(40)
            .jsonPath("$.[0].parameters.%s.minIntensity", formattedDates[1]).isEqualTo(40)
            .jsonPath("$.[0].parameters.%s.maxIntensity", formattedDates[1]).isEqualTo(40)
    }
}