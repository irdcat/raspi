package irdcat.fitness.api

import irdcat.fitness.Constants.RequestParameters
import irdcat.fitness.service.model.Exercise
import irdcat.fitness.service.model.TrainingExercise
import irdcat.fitness.service.model.TrainingExerciseSet
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType

class ExerciseApiTests: AbstractApiTest() {

    @BeforeEach
    fun beforeEach() {
        cleanupTrainingExercises()
    }

    @Test
    fun getExerciseNames() {

        insertTrainingExercises(listOf(
            TrainingExercise("1", 1, Exercise("Pull Up", true), 63.0f, "2025-01-01".toLocalDate(), listOf()),
            TrainingExercise("2", 2, Exercise("Deadlift", false), 63.0f, "2025-01-01".toLocalDate(), listOf()),
            TrainingExercise("3", 1, Exercise("Pull Up", true), 63.0f, "2025-01-02".toLocalDate(), listOf())
        ))

        webTestClient()
            .get()
            .uri("/api/exercises/names")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$").isArray
            .jsonPath("$[0]").isEqualTo("Deadlift")
            .jsonPath("$[1]").isEqualTo("Pull Up")
            .jsonPath("$[2]").doesNotExist()
    }

    @Test
    fun getCountedExercises_empty() {

        webTestClient()
            .get()
            .uri { builder ->
                builder
                    .path("/api/exercises/counted")
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
            .jsonPath("$.totalResults").isEqualTo(0)
            .jsonPath("$.content").isEmpty
    }

    @Test
    fun getCountedExercises_resultsInOnePage() {

        insertTrainingExercises(listOf(
            TrainingExercise("1", 1, Exercise("Pull Up", true), 63.0f, "2025-01-01".toLocalDate(), listOf()),
            TrainingExercise("2", 2, Exercise("Deadlift", false), 63.0f, "2025-01-01".toLocalDate(), listOf()),
            TrainingExercise("3", 1, Exercise("Pull Up", true), 63.0f, "2025-01-02".toLocalDate(), listOf())
        ))

        webTestClient()
            .get()
            .uri { builder ->
                builder
                    .path("/api/exercises/counted")
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
            .jsonPath("$.content[0].exercise.name").isEqualTo("Deadlift")
            .jsonPath("$.content[0].exercise.isBodyweight").isEqualTo(false)
            .jsonPath("$.content[0].count").isEqualTo(1)
            .jsonPath("$.content[1].exercise.name").isEqualTo("Pull Up")
            .jsonPath("$.content[1].exercise.isBodyweight").isEqualTo(true)
            .jsonPath("$.content[1].count").isEqualTo(2)
            .jsonPath("$.content[2]").doesNotExist()
    }

    @Test
    fun getCountedExercises_resultsInTwoPages() {

        insertTrainingExercises(listOf(
            TrainingExercise("1", 1, Exercise("Pull Up", true), 63.0f, "2025-01-01".toLocalDate(), listOf()),
            TrainingExercise("2", 2, Exercise("Deadlift", false), 63.0f, "2025-01-01".toLocalDate(), listOf()),
            TrainingExercise("3", 1, Exercise("Pull Up", true), 63.0f, "2025-01-02".toLocalDate(), listOf())
        ))

        webTestClient()
            .get()
            .uri { builder ->
                builder
                    .path("/api/exercises/counted")
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
            .jsonPath("$.content[0].exercise.name").isEqualTo("Deadlift")
            .jsonPath("$.content[0].exercise.isBodyweight").isEqualTo(false)
            .jsonPath("$.content[0].count").isEqualTo(1)
            .jsonPath("$.content[1]").doesNotExist()

        webTestClient()
            .get()
            .uri { builder ->
                builder
                    .path("/api/exercises/counted")
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
            .jsonPath("$.content[0].exercise.name").isEqualTo("Pull Up")
            .jsonPath("$.content[0].exercise.isBodyweight").isEqualTo(true)
            .jsonPath("$.content[0].count").isEqualTo(2)
            .jsonPath("$.content[1]").doesNotExist()
    }

    @Test
    fun getCountedExercises_maxIsCalculated() {

        insertTrainingExercises(listOf(
            TrainingExercise("1", 0, Exercise("Deadlift", false), 60.0f, "2025-01-01".toLocalDate(), listOf(
                TrainingExerciseSet(1, 110.0f),
                TrainingExerciseSet(1, 115.0f),
                TrainingExerciseSet(1, 120.0f)
            ))
        ))

        webTestClient()
            .get()
            .uri { builder ->
                builder
                    .path("/api/exercises/counted")
                    .queryParam("name", "Deadlift")
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
            .jsonPath("$.totalResults").isEqualTo(1)
            .jsonPath("$.content").isArray
            .jsonPath("$.content[0].exercise.name").isEqualTo("Deadlift")
            .jsonPath("$.content[0].exercise.isBodyweight").isEqualTo(false)
            .jsonPath("$.content[0].best").isEqualTo(120.0f)
            .jsonPath("$.content[0].count").isEqualTo(1)
            .jsonPath("$.content[1]").doesNotExist()
    }
}
