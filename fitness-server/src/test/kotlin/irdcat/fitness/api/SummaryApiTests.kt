package irdcat.fitness.api

import irdcat.fitness.service.BodyweightSummaryDto
import irdcat.fitness.service.Exercise
import irdcat.fitness.service.ExerciseSummaryDto
import irdcat.fitness.service.ExerciseSummaryParametersDto
import irdcat.fitness.service.TrainingExercise
import irdcat.fitness.service.TrainingExerciseSet
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType

class SummaryApiTests: AbstractApiTest() {

    @BeforeEach
    fun beforeEach() {
        cleanupTrainingExercises()
    }

    @Test
    fun getBodyweightSummary_ok() {

        insertTrainingExercises(listOf(
            TrainingExercise("1", 1, Exercise("Pull Up", true), 63.0f, "2025-01-01".toLocalDate(), listOf()),
            TrainingExercise("2", 2, Exercise("Deadlift", false), 63.0f, "2025-01-01".toLocalDate(), listOf()),
            TrainingExercise("3", 1, Exercise("Pull Up", true), 64.0f, "2025-01-02".toLocalDate(), listOf())
        ))

        val nullableResult = webTestClient()
            .get()
            .uri { builder ->
                builder
                    .path("/api/summary/bodyweight")
                    .queryParam("from", "2025-01-01")
                    .queryParam("to", "2025-01-02")
                    .build()
            }
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .returnResult(BodyweightSummaryDto::class.java)
            .responseBody
            .next()
            .block()

        assertNotNull(nullableResult)
        val result = nullableResult!!
        assertEquals(result.parameters.size, 2)
        assertEquals(result.parameters["2025-01-01"], 63.0f)
        assertEquals(result.parameters["2025-01-02"], 64.0f)
    }

    @Test
    fun getBodyweightSummary_empty() {

        val nullableResult = webTestClient()
            .get()
            .uri { builder ->
                builder
                    .path("/api/summary/bodyweight")
                    .queryParam("from", "2025-01-01")
                    .queryParam("to", "2025-01-02")
                    .build()
            }
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .returnResult(BodyweightSummaryDto::class.java)
            .responseBody
            .next()
            .block()

        assertNotNull(nullableResult)
        val result = nullableResult!!
        assertEquals(result.parameters.size, 0)
    }

    @Test
    fun getExerciseSummary_empty() {

        val nullableResult = webTestClient()
            .get()
            .uri { builder ->
                builder
                    .path("/api/summary/exercise")
                    .queryParam("from", "2025-01-01")
                    .queryParam("to", "2025-01-02")
                    .queryParam("name", "Pull Up")
                    .build()
            }
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .returnResult(ExerciseSummaryDto::class.java)
            .responseBody
            .next()
            .block()

        assertNotNull(nullableResult)
        val result = nullableResult!!
        assertEquals(result.exercise.name, "Pull Up")
        assertEquals(result.exercise.isBodyweight, false)
        assertEquals(result.parameters.size, 0)
    }

    @Test
    fun getExerciseSummary_bodyweightExercise() {

        insertTrainingExercises(listOf(
            TrainingExercise("1", 1, Exercise("Pull Up", true), 63.0f, "2025-01-01".toLocalDate(), listOf(
                TrainingExerciseSet(10, 5.0f),
                TrainingExerciseSet(10, 5.0f)
            )),
            TrainingExercise("2", 2, Exercise("Deadlift", false), 63.0f, "2025-01-01".toLocalDate(), listOf(
                TrainingExerciseSet(5, 120.0f),
                TrainingExerciseSet(5, 120.0f)
            )),
            TrainingExercise("3", 1, Exercise("Pull Up", true), 64.0f, "2025-01-02".toLocalDate(), listOf(
                TrainingExerciseSet(10, 6.0f),
                TrainingExerciseSet(10, 6.0f)
            ))
        ))

        val nullableResult = webTestClient()
            .get()
            .uri { builder ->
                builder
                    .path("/api/summary/exercise")
                    .queryParam("from", "2025-01-01")
                    .queryParam("to", "2025-01-02")
                    .queryParam("name", "Pull Up")
                    .build()
            }
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .returnResult(ExerciseSummaryDto::class.java)
            .responseBody
            .next()
            .block()

        assertNotNull(nullableResult)
        val result = nullableResult!!
        assertEquals(result.exercise.name, "Pull Up")
        assertEquals(result.exercise.isBodyweight, true)
        assertEquals(result.parameters.size, 2)
        assertEquals(result.parameters["2025-01-01"], ExerciseSummaryParametersDto(
            volume = 100.0f,
            averageVolume = 50.0f,
            minIntensity = 5.0f,
            maxIntensity = 5.0f,
            averageIntensity = 5.0f,
            bodyweight = 63.0f,
            bodyweightVolume = 1260.0f,
            averageBodyweightVolume = 630.0f
        ))
        assertEquals(result.parameters["2025-01-02"], ExerciseSummaryParametersDto(
            volume = 120.0f,
            averageVolume = 60.0f,
            minIntensity = 6.0f,
            maxIntensity = 6.0f,
            averageIntensity = 6.0f,
            bodyweight = 64.0f,
            bodyweightVolume = 1280.0f,
            averageBodyweightVolume = 640.0f
        ))
    }

    @Test
    fun getExerciseSummary_nonBodyweightExercise() {

        insertTrainingExercises(listOf(
            TrainingExercise("1", 1, Exercise("Pull Up", true), 63.0f, "2025-01-01".toLocalDate(), listOf(
                TrainingExerciseSet(10, 5.0f),
                TrainingExerciseSet(10, 5.0f)
            )),
            TrainingExercise("2", 2, Exercise("Deadlift", false), 63.0f, "2025-01-01".toLocalDate(), listOf(
                TrainingExerciseSet(5, 120.0f),
                TrainingExerciseSet(5, 120.0f)
            )),
            TrainingExercise("3", 1, Exercise("Pull Up", true), 64.0f, "2025-01-02".toLocalDate(), listOf(
                TrainingExerciseSet(10, 6.0f),
                TrainingExerciseSet(10, 6.0f)
            ))
        ))

        val nullableResult = webTestClient()
            .get()
            .uri { builder ->
                builder
                    .path("/api/summary/exercise")
                    .queryParam("from", "2025-01-01")
                    .queryParam("to", "2025-01-02")
                    .queryParam("name", "Deadlift")
                    .build()
            }
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .returnResult(ExerciseSummaryDto::class.java)
            .responseBody
            .next()
            .block()

        assertNotNull(nullableResult)
        val result = nullableResult!!
        assertEquals(result.exercise.name, "Deadlift")
        assertEquals(result.exercise.isBodyweight, false)
        assertEquals(result.parameters.size, 1)
        assertEquals(result.parameters["2025-01-01"], ExerciseSummaryParametersDto(
            volume = 1200.0f,
            averageVolume = 600.0f,
            minIntensity = 120.0f,
            maxIntensity = 120.0f,
            averageIntensity = 120.0f,
            bodyweight = 63.0f
        ))
    }
}