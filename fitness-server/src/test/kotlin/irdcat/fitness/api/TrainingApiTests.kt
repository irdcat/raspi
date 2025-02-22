package irdcat.fitness.api

import irdcat.fitness.Constants.RequestParameters
import irdcat.fitness.service.Exercise
import irdcat.fitness.service.TrainingExercise
import irdcat.fitness.service.TrainingExerciseSet
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType

class TrainingApiTests: AbstractApiTest() {

    @BeforeEach
    fun beforeEach() {
        cleanupTrainingExercises()
    }

    @Test
    fun getTraining() {

        insertTrainingExercises(listOf(
            TrainingExercise("1", Exercise("Pull Up", true), 63.0f, "01.01.2025".toDate(), listOf(
                TrainingExerciseSet(10, 5.0f)
            )),
            TrainingExercise("2", Exercise("Deadlift", false), 63.0f, "01.01.2025".toDate(), listOf(
                TrainingExerciseSet(5, 120.0f)
            )),
            TrainingExercise("3", Exercise("Pull Up", true), 63.0f, "02.01.2025".toDate(), listOf(
                TrainingExerciseSet(10, 6.0f)
            ))
        ))

        webTestClient()
            .get()
            .uri("/api/trainings/2025-01-01")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.date").isEqualTo("2025-01-01")
            .jsonPath("$.bodyweight").isEqualTo(63.0f)
            .jsonPath("$.exercises").isArray
            .jsonPath("$.exercises[0].id").isEqualTo("1")
            .jsonPath("$.exercises[0].exercise.name").isEqualTo("Pull Up")
            .jsonPath("$.exercises[0].exercise.isBodyweight").isEqualTo(true)
            .jsonPath("$.exercises[0].sets").isArray
            .jsonPath("$.exercises[0].sets[0].repetitions").isEqualTo(10)
            .jsonPath("$.exercises[0].sets[0].weight").isEqualTo(5.0f)
            .jsonPath("$.exercises[0].sets[1]").doesNotExist()
            .jsonPath("$.exercises[1].id").isEqualTo("2")
            .jsonPath("$.exercises[1].exercise.name").isEqualTo("Deadlift")
            .jsonPath("$.exercises[1].exercise.isBodyweight").isEqualTo(false)
            .jsonPath("$.exercises[1].sets").isArray
            .jsonPath("$.exercises[1].sets[0].repetitions").isEqualTo(5)
            .jsonPath("$.exercises[1].sets[0].weight").isEqualTo(120.0f)
            .jsonPath("$.exercises[1].sets[1]").doesNotExist()
            .jsonPath("$.exercises[2]").doesNotExist()
    }

    @Test
    fun getTrainings_resultsInOnePage() {

        insertTrainingExercises(listOf(
            TrainingExercise("1", Exercise("Pull Up", true), 63.0f, "01.01.2025".toDate(), listOf(
                TrainingExerciseSet(10, 5.0f)
            )),
            TrainingExercise("2", Exercise("Deadlift", false), 63.0f, "01.01.2025".toDate(), listOf(
                TrainingExerciseSet(5, 120.0f)
            )),
            TrainingExercise("3", Exercise("Pull Up", true), 63.0f, "02.01.2025".toDate(), listOf(
                TrainingExerciseSet(10, 6.0f)
            ))
        ))

        webTestClient()
            .get()
            .uri { builder ->
                builder
                    .path("/api/trainings")
                    .queryParam("from", "2025-01-01")
                    .queryParam("to", "2025-01-02")
                    .queryParam(RequestParameters.PAGE, 0)
                    .queryParam(RequestParameters.SIZE, 25)
                    .build()
            }
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.currentPage").isEqualTo(0)
            .jsonPath("$.pageSize").isEqualTo(25)
            .jsonPath("$.totalResults").isEqualTo(2)
            .jsonPath("$.content").isArray
            .jsonPath("$.content[0].date").isEqualTo("2025-01-02")
            .jsonPath("$.content[0].bodyweight").isEqualTo(63.0f)
            .jsonPath("$.content[0].exercises").isArray
            .jsonPath("$.content[0].exercises[0].id").isEqualTo("3")
            .jsonPath("$.content[0].exercises[0].exercise.name").isEqualTo("Pull Up")
            .jsonPath("$.content[0].exercises[0].exercise.isBodyweight").isEqualTo(true)
            .jsonPath("$.content[0].exercises[0].sets").isArray
            .jsonPath("$.content[0].exercises[0].sets[0].repetitions").isEqualTo(10)
            .jsonPath("$.content[0].exercises[0].sets[0].weight").isEqualTo(6.0f)
            .jsonPath("$.content[0].exercises[0].sets[1]").doesNotExist()
            .jsonPath("$.content[0].exercises[1]").doesNotExist()
            .jsonPath("$.content[1].date").isEqualTo("2025-01-01")
            .jsonPath("$.content[1].bodyweight").isEqualTo(63.0f)
            .jsonPath("$.content[1].exercises").isArray
            .jsonPath("$.content[1].exercises[0].id").isEqualTo("1")
            .jsonPath("$.content[1].exercises[0].exercise.name").isEqualTo("Pull Up")
            .jsonPath("$.content[1].exercises[0].exercise.isBodyweight").isEqualTo(true)
            .jsonPath("$.content[1].exercises[0].sets").isArray
            .jsonPath("$.content[1].exercises[0].sets[0].repetitions").isEqualTo(10)
            .jsonPath("$.content[1].exercises[0].sets[0].weight").isEqualTo(5.0f)
            .jsonPath("$.content[1].exercises[0].sets[1]").doesNotExist()
            .jsonPath("$.content[1].exercises[1].id").isEqualTo("2")
            .jsonPath("$.content[1].exercises[1].exercise.name").isEqualTo("Deadlift")
            .jsonPath("$.content[1].exercises[1].exercise.isBodyweight").isEqualTo(false)
            .jsonPath("$.content[1].exercises[1].sets").isArray
            .jsonPath("$.content[1].exercises[1].sets[0].repetitions").isEqualTo(5)
            .jsonPath("$.content[1].exercises[1].sets[0].weight").isEqualTo(120.0f)
            .jsonPath("$.content[1].exercises[1].sets[1]").doesNotExist()
            .jsonPath("$.content[1].exercises[2]").doesNotExist()
            .jsonPath("$.content[2]").doesNotExist()
    }

    @Test
    fun getTrainings_resultsInTwoPages() {

        insertTrainingExercises(listOf(
            TrainingExercise("1", Exercise("Pull Up", true), 63.0f, "01.01.2025".toDate(), listOf(
                TrainingExerciseSet(10, 5.0f)
            )),
            TrainingExercise("2", Exercise("Deadlift", false), 63.0f, "01.01.2025".toDate(), listOf(
                TrainingExerciseSet(5, 120.0f)
            )),
            TrainingExercise("3", Exercise("Pull Up", true), 63.0f, "02.01.2025".toDate(), listOf(
                TrainingExerciseSet(10, 6.0f)
            ))
        ))

        webTestClient()
            .get()
            .uri { builder ->
                builder
                    .path("/api/trainings")
                    .queryParam("from", "2025-01-01")
                    .queryParam("to", "2025-01-02")
                    .queryParam(RequestParameters.PAGE, 0)
                    .queryParam(RequestParameters.SIZE, 1)
                    .build()
            }
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.currentPage").isEqualTo(0)
            .jsonPath("$.pageSize").isEqualTo(1)
            .jsonPath("$.totalResults").isEqualTo(2)
            .jsonPath("$.content").isArray
            .jsonPath("$.content[0].date").isEqualTo("2025-01-02")
            .jsonPath("$.content[0].bodyweight").isEqualTo(63.0f)
            .jsonPath("$.content[0].exercises").isArray
            .jsonPath("$.content[0].exercises[0].id").isEqualTo("3")
            .jsonPath("$.content[0].exercises[0].exercise.name").isEqualTo("Pull Up")
            .jsonPath("$.content[0].exercises[0].exercise.isBodyweight").isEqualTo(true)
            .jsonPath("$.content[0].exercises[0].sets").isArray
            .jsonPath("$.content[0].exercises[0].sets[0].repetitions").isEqualTo(10)
            .jsonPath("$.content[0].exercises[0].sets[0].weight").isEqualTo(6.0f)
            .jsonPath("$.content[0].exercises[0].sets[1]").doesNotExist()
            .jsonPath("$.content[0].exercises[1]").doesNotExist()
            .jsonPath("$.content[1]").doesNotExist()

        webTestClient()
            .get()
            .uri { builder ->
                builder
                    .path("/api/trainings")
                    .queryParam("from", "2025-01-01")
                    .queryParam("to", "2025-01-02")
                    .queryParam(RequestParameters.PAGE, 1)
                    .queryParam(RequestParameters.SIZE, 1)
                    .build()
            }
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.currentPage").isEqualTo(1)
            .jsonPath("$.pageSize").isEqualTo(1)
            .jsonPath("$.totalResults").isEqualTo(2)
            .jsonPath("$.content").isArray
            .jsonPath("$.content[0].date").isEqualTo("2025-01-01")
            .jsonPath("$.content[0].bodyweight").isEqualTo(63.0f)
            .jsonPath("$.content[0].exercises").isArray
            .jsonPath("$.content[0].exercises[0].id").isEqualTo("1")
            .jsonPath("$.content[0].exercises[0].exercise.name").isEqualTo("Pull Up")
            .jsonPath("$.content[0].exercises[0].exercise.isBodyweight").isEqualTo(true)
            .jsonPath("$.content[0].exercises[0].sets").isArray
            .jsonPath("$.content[0].exercises[0].sets[0].repetitions").isEqualTo(10)
            .jsonPath("$.content[0].exercises[0].sets[0].weight").isEqualTo(5.0f)
            .jsonPath("$.content[0].exercises[0].sets[1]").doesNotExist()
            .jsonPath("$.content[0].exercises[1].id").isEqualTo("2")
            .jsonPath("$.content[0].exercises[1].exercise.name").isEqualTo("Deadlift")
            .jsonPath("$.content[0].exercises[1].exercise.isBodyweight").isEqualTo(false)
            .jsonPath("$.content[0].exercises[1].sets").isArray
            .jsonPath("$.content[0].exercises[1].sets[0].repetitions").isEqualTo(5)
            .jsonPath("$.content[0].exercises[1].sets[0].weight").isEqualTo(120.0f)
            .jsonPath("$.content[0].exercises[1].sets[1]").doesNotExist()
            .jsonPath("$.content[0].exercises[2]").doesNotExist()
            .jsonPath("$.content[1]").doesNotExist()
    }
}