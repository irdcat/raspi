package irdcat.fitness.api

import irdcat.fitness.Constants.RequestParameters
import irdcat.fitness.service.Exercise
import irdcat.fitness.service.ExerciseDto
import irdcat.fitness.service.TrainingDto
import irdcat.fitness.service.TrainingExercise
import irdcat.fitness.service.TrainingExerciseDto
import irdcat.fitness.service.TrainingExerciseSet
import irdcat.fitness.service.TrainingExerciseSetDto
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType

class TrainingApiTests: AbstractApiTest() {

    @BeforeEach
    fun beforeEach() {
        cleanupTrainingExercises()
    }

    @Test
    fun getTraining_ok() {

        insertTrainingExercises(listOf(
            TrainingExercise("1", 1, Exercise("Pull Up", true), 63.0f, "2025-01-01".toLocalDate(), listOf(
                TrainingExerciseSet(10, 5.0f)
            )),
            TrainingExercise("2", 2, Exercise("Deadlift", false), 63.0f, "2025-01-01".toLocalDate(), listOf(
                TrainingExerciseSet(5, 120.0f)
            )),
            TrainingExercise("3", 1, Exercise("Pull Up", true), 63.0f, "2025-01-02".toLocalDate(), listOf(
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
            .jsonPath("$.exercises[0].order").isEqualTo(1)
            .jsonPath("$.exercises[0].exercise.name").isEqualTo("Pull Up")
            .jsonPath("$.exercises[0].exercise.isBodyweight").isEqualTo(true)
            .jsonPath("$.exercises[0].sets").isArray
            .jsonPath("$.exercises[0].sets[0].repetitions").isEqualTo(10)
            .jsonPath("$.exercises[0].sets[0].weight").isEqualTo(5.0f)
            .jsonPath("$.exercises[0].sets[1]").doesNotExist()
            .jsonPath("$.exercises[1].id").isEqualTo("2")
            .jsonPath("$.exercises[1].order").isEqualTo(2)
            .jsonPath("$.exercises[1].exercise.name").isEqualTo("Deadlift")
            .jsonPath("$.exercises[1].exercise.isBodyweight").isEqualTo(false)
            .jsonPath("$.exercises[1].sets").isArray
            .jsonPath("$.exercises[1].sets[0].repetitions").isEqualTo(5)
            .jsonPath("$.exercises[1].sets[0].weight").isEqualTo(120.0f)
            .jsonPath("$.exercises[1].sets[1]").doesNotExist()
            .jsonPath("$.exercises[2]").doesNotExist()
    }

    @Test
    fun getTraining_notFound() {

        insertTrainingExercises(listOf(
            TrainingExercise("1", 1, Exercise("Pull Up", true), 63.0f, "2025-01-01".toLocalDate(), listOf(
                TrainingExerciseSet(10, 5.0f)
            )),
            TrainingExercise("2", 2, Exercise("Deadlift", false), 63.0f, "2025-01-01".toLocalDate(), listOf(
                TrainingExerciseSet(5, 120.0f)
            )),
            TrainingExercise("3", 1, Exercise("Pull Up", true), 63.0f, "2025-01-02".toLocalDate(), listOf(
                TrainingExerciseSet(10, 6.0f)
            ))
        ))

        webTestClient()
            .get()
            .uri("/api/trainings/2025-01-03")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNotFound
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.timestamp").isNotEmpty
            .jsonPath("$.path").isEqualTo("/api/trainings/2025-01-03")
            .jsonPath("$.status").isEqualTo(404)
            .jsonPath("$.error").isNotEmpty
            .jsonPath("$.requestId").isNotEmpty
    }

    @Test
    fun getTrainings_empty() {

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
            .jsonPath("$.totalResults").isEqualTo(0)
            .jsonPath("$.content").isEmpty
    }

    @Test
    fun getTrainings_resultsInOnePage() {

        insertTrainingExercises(listOf(
            TrainingExercise("1", 1, Exercise("Pull Up", true), 63.0f, "2025-01-01".toLocalDate(), listOf(
                TrainingExerciseSet(10, 5.0f)
            )),
            TrainingExercise("2", 2, Exercise("Deadlift", false), 63.0f, "2025-01-01".toLocalDate(), listOf(
                TrainingExerciseSet(5, 120.0f)
            )),
            TrainingExercise("3", 1, Exercise("Pull Up", true), 63.0f, "2025-01-02".toLocalDate(), listOf(
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
            .jsonPath("$.content[0].exercises[0].order").isEqualTo(1)
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
            .jsonPath("$.content[1].exercises[0].order").isEqualTo(1)
            .jsonPath("$.content[1].exercises[0].exercise.name").isEqualTo("Pull Up")
            .jsonPath("$.content[1].exercises[0].exercise.isBodyweight").isEqualTo(true)
            .jsonPath("$.content[1].exercises[0].sets").isArray
            .jsonPath("$.content[1].exercises[0].sets[0].repetitions").isEqualTo(10)
            .jsonPath("$.content[1].exercises[0].sets[0].weight").isEqualTo(5.0f)
            .jsonPath("$.content[1].exercises[0].sets[1]").doesNotExist()
            .jsonPath("$.content[1].exercises[1].id").isEqualTo("2")
            .jsonPath("$.content[1].exercises[1].order").isEqualTo(2)
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
            TrainingExercise("1", 1, Exercise("Pull Up", true), 63.0f, "2025-01-01".toLocalDate(), listOf(
                TrainingExerciseSet(10, 5.0f)
            )),
            TrainingExercise("2", 2, Exercise("Deadlift", false), 63.0f, "2025-01-01".toLocalDate(), listOf(
                TrainingExerciseSet(5, 120.0f)
            )),
            TrainingExercise("3", 1, Exercise("Pull Up", true), 63.0f, "2025-01-02".toLocalDate(), listOf(
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
            .jsonPath("$.content[0].exercises[0].order").isEqualTo(1)
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
            .jsonPath("$.content[0].exercises[0].order").isEqualTo(1)
            .jsonPath("$.content[0].exercises[0].exercise.name").isEqualTo("Pull Up")
            .jsonPath("$.content[0].exercises[0].exercise.isBodyweight").isEqualTo(true)
            .jsonPath("$.content[0].exercises[0].sets").isArray
            .jsonPath("$.content[0].exercises[0].sets[0].repetitions").isEqualTo(10)
            .jsonPath("$.content[0].exercises[0].sets[0].weight").isEqualTo(5.0f)
            .jsonPath("$.content[0].exercises[0].sets[1]").doesNotExist()
            .jsonPath("$.content[0].exercises[1].id").isEqualTo("2")
            .jsonPath("$.content[0].exercises[1].order").isEqualTo(2)
            .jsonPath("$.content[0].exercises[1].exercise.name").isEqualTo("Deadlift")
            .jsonPath("$.content[0].exercises[1].exercise.isBodyweight").isEqualTo(false)
            .jsonPath("$.content[0].exercises[1].sets").isArray
            .jsonPath("$.content[0].exercises[1].sets[0].repetitions").isEqualTo(5)
            .jsonPath("$.content[0].exercises[1].sets[0].weight").isEqualTo(120.0f)
            .jsonPath("$.content[0].exercises[1].sets[1]").doesNotExist()
            .jsonPath("$.content[0].exercises[2]").doesNotExist()
            .jsonPath("$.content[1]").doesNotExist()
    }

    @Test
    fun createTraining() {

        val newTrainingDto = TrainingDto(
            "2025-01-04".toLocalDate(),
            65.0f,
            listOf(
                TrainingExerciseDto(
                    "", 1, ExerciseDto("Pull Up", true), listOf(
                        TrainingExerciseSetDto(10, 7.0f),
                        TrainingExerciseSetDto(10, 7.0f),
                        TrainingExerciseSetDto(10, 7.0f)
                    )
                ),
                TrainingExerciseDto(
                    "", 2, ExerciseDto("Dip", true), listOf(
                        TrainingExerciseSetDto(10, 20.0f),
                        TrainingExerciseSetDto(10, 20.0f),
                        TrainingExerciseSetDto(10, 20.0f)
                    )
                )
            )
        )

        webTestClient()
            .post()
            .uri("/api/trainings")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(newTrainingDto)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.date").isEqualTo("2025-01-04")
            .jsonPath("$.bodyweight").isEqualTo(65.0f)
            .jsonPath("$.exercises").isArray
            .jsonPath("$.exercises[0].id").isNotEmpty
            .jsonPath("$.exercises[0].order").isEqualTo(1)
            .jsonPath("$.exercises[0].exercise.name").isEqualTo("Pull Up")
            .jsonPath("$.exercises[0].exercise.isBodyweight").isEqualTo(true)
            .jsonPath("$.exercises[0].sets").isArray
            .jsonPath("$.exercises[0].sets[0].repetitions").isEqualTo(10)
            .jsonPath("$.exercises[0].sets[0].weight").isEqualTo(7.0f)
            .jsonPath("$.exercises[0].sets[1].repetitions").isEqualTo(10)
            .jsonPath("$.exercises[0].sets[1].weight").isEqualTo(7.0f)
            .jsonPath("$.exercises[0].sets[2].repetitions").isEqualTo(10)
            .jsonPath("$.exercises[0].sets[2].weight").isEqualTo(7.0f)
            .jsonPath("$.exercises[0].sets[3]").doesNotExist()
            .jsonPath("$.exercises[1].id").isNotEmpty
            .jsonPath("$.exercises[1].order").isEqualTo(2)
            .jsonPath("$.exercises[1].exercise.name").isEqualTo("Dip")
            .jsonPath("$.exercises[1].exercise.isBodyweight").isEqualTo(true)
            .jsonPath("$.exercises[1].sets").isArray
            .jsonPath("$.exercises[1].sets[0].repetitions").isEqualTo(10)
            .jsonPath("$.exercises[1].sets[0].weight").isEqualTo(20.0f)
            .jsonPath("$.exercises[1].sets[1].repetitions").isEqualTo(10)
            .jsonPath("$.exercises[1].sets[1].weight").isEqualTo(20.0f)
            .jsonPath("$.exercises[1].sets[2].repetitions").isEqualTo(10)
            .jsonPath("$.exercises[1].sets[2].weight").isEqualTo(20.0f)
            .jsonPath("$.exercises[1].sets[3]").doesNotExist()
            .jsonPath("$.exercises[2]").doesNotExist()

        webTestClient()
            .get()
            .uri("/api/trainings/2025-01-04")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.date").isEqualTo("2025-01-04")
            .jsonPath("$.bodyweight").isEqualTo(65.0f)
            .jsonPath("$.exercises").isArray
            .jsonPath("$.exercises[0].id").isNotEmpty
            .jsonPath("$.exercises[0].order").isEqualTo(1)
            .jsonPath("$.exercises[0].exercise.name").isEqualTo("Pull Up")
            .jsonPath("$.exercises[0].exercise.isBodyweight").isEqualTo(true)
            .jsonPath("$.exercises[0].sets").isArray
            .jsonPath("$.exercises[0].sets[0].repetitions").isEqualTo(10)
            .jsonPath("$.exercises[0].sets[0].weight").isEqualTo(7.0f)
            .jsonPath("$.exercises[0].sets[1].repetitions").isEqualTo(10)
            .jsonPath("$.exercises[0].sets[1].weight").isEqualTo(7.0f)
            .jsonPath("$.exercises[0].sets[2].repetitions").isEqualTo(10)
            .jsonPath("$.exercises[0].sets[2].weight").isEqualTo(7.0f)
            .jsonPath("$.exercises[0].sets[3]").doesNotExist()
            .jsonPath("$.exercises[1].id").isNotEmpty
            .jsonPath("$.exercises[1].order").isEqualTo(2)
            .jsonPath("$.exercises[1].exercise.name").isEqualTo("Dip")
            .jsonPath("$.exercises[1].exercise.isBodyweight").isEqualTo(true)
            .jsonPath("$.exercises[1].sets").isArray
            .jsonPath("$.exercises[1].sets[0].repetitions").isEqualTo(10)
            .jsonPath("$.exercises[1].sets[0].weight").isEqualTo(20.0f)
            .jsonPath("$.exercises[1].sets[1].repetitions").isEqualTo(10)
            .jsonPath("$.exercises[1].sets[1].weight").isEqualTo(20.0f)
            .jsonPath("$.exercises[1].sets[2].repetitions").isEqualTo(10)
            .jsonPath("$.exercises[1].sets[2].weight").isEqualTo(20.0f)
            .jsonPath("$.exercises[1].sets[3]").doesNotExist()
            .jsonPath("$.exercises[2]").doesNotExist()
    }

    @Test
    fun updateTraining_modifyExercise() {

        insertTrainingExercises(listOf(
            TrainingExercise("1", 1, Exercise("Pull Up", true), 63.0f, "2025-01-01".toLocalDate(), listOf(
                TrainingExerciseSet(10, 5.0f)
            )),
            TrainingExercise("2", 2, Exercise("Deadlift", false), 63.0f, "2025-01-01".toLocalDate(), listOf(
                TrainingExerciseSet(5, 120.0f)
            ))
        ))

        val newTrainingDto = TrainingDto(
            "2025-01-01".toLocalDate(),
            63.0f,
            listOf(
                TrainingExerciseDto(
                    "1", 1, ExerciseDto("Dip", true), listOf(
                        TrainingExerciseSetDto(10, 20.0f)
                    )
                ),
                TrainingExerciseDto(
                    "2", 2, ExerciseDto("Deadlift", false), listOf(
                        TrainingExerciseSetDto(5, 120.0f)
                    )
                )
            )
        )

        webTestClient()
            .post()
            .uri("/api/trainings")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(newTrainingDto)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.date").isEqualTo("2025-01-01")
            .jsonPath("$.bodyweight").isEqualTo(63.0f)
            .jsonPath("$.exercises").isArray
            .jsonPath("$.exercises[0].id").isEqualTo("1")
            .jsonPath("$.exercises[0].order").isEqualTo(1)
            .jsonPath("$.exercises[0].exercise.name").isEqualTo("Dip")
            .jsonPath("$.exercises[0].exercise.isBodyweight").isEqualTo(true)
            .jsonPath("$.exercises[0].sets").isArray
            .jsonPath("$.exercises[0].sets[0].repetitions").isEqualTo(10)
            .jsonPath("$.exercises[0].sets[0].weight").isEqualTo(20.0f)
            .jsonPath("$.exercises[0].sets[1]").doesNotExist()
            .jsonPath("$.exercises[1].id").isEqualTo("2")
            .jsonPath("$.exercises[1].order").isEqualTo(2)
            .jsonPath("$.exercises[1].exercise.name").isEqualTo("Deadlift")
            .jsonPath("$.exercises[1].exercise.isBodyweight").isEqualTo(false)
            .jsonPath("$.exercises[1].sets").isArray
            .jsonPath("$.exercises[1].sets[0].repetitions").isEqualTo(5)
            .jsonPath("$.exercises[1].sets[0].weight").isEqualTo(120.0f)
            .jsonPath("$.exercises[1].sets[1]").doesNotExist()
            .jsonPath("$.exercises[2]").doesNotExist()

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
            .jsonPath("$.exercises[0].order").isEqualTo(1)
            .jsonPath("$.exercises[0].exercise.name").isEqualTo("Dip")
            .jsonPath("$.exercises[0].exercise.isBodyweight").isEqualTo(true)
            .jsonPath("$.exercises[0].sets").isArray
            .jsonPath("$.exercises[0].sets[0].repetitions").isEqualTo(10)
            .jsonPath("$.exercises[0].sets[0].weight").isEqualTo(20.0f)
            .jsonPath("$.exercises[0].sets[1]").doesNotExist()
            .jsonPath("$.exercises[1].id").isEqualTo("2")
            .jsonPath("$.exercises[1].order").isEqualTo(2)
            .jsonPath("$.exercises[1].exercise.name").isEqualTo("Deadlift")
            .jsonPath("$.exercises[1].exercise.isBodyweight").isEqualTo(false)
            .jsonPath("$.exercises[1].sets").isArray
            .jsonPath("$.exercises[1].sets[0].repetitions").isEqualTo(5)
            .jsonPath("$.exercises[1].sets[0].weight").isEqualTo(120.0f)
            .jsonPath("$.exercises[1].sets[1]").doesNotExist()
            .jsonPath("$.exercises[2]").doesNotExist()
    }

    @Test
    fun updateTraining_addExercise() {
        insertTrainingExercises(listOf(
            TrainingExercise("1", 1, Exercise("Dip", true), 63.0f, "2025-01-01".toLocalDate(), listOf(
                TrainingExerciseSet(10, 20.0f)
            ))
        ))

        val newTrainingDto = TrainingDto(
            "2025-01-01".toLocalDate(),
            63.0f,
            listOf(
                TrainingExerciseDto("1", 1, ExerciseDto("Dip", true), listOf(
                    TrainingExerciseSetDto(10, 20.0f)
                )),
                TrainingExerciseDto("", 2, ExerciseDto("Pull Up", true), listOf(
                    TrainingExerciseSetDto(10, 5.0f)
                ))
            )
        )

        webTestClient()
            .post()
            .uri("/api/trainings")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(newTrainingDto)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.date").isEqualTo("2025-01-01")
            .jsonPath("$.bodyweight").isEqualTo(63.0f)
            .jsonPath("$.exercises").isArray
            .jsonPath("$.exercises[0].id").isEqualTo("1")
            .jsonPath("$.exercises[0].order").isEqualTo(1)
            .jsonPath("$.exercises[0].exercise.name").isEqualTo("Dip")
            .jsonPath("$.exercises[0].exercise.isBodyweight").isEqualTo(true)
            .jsonPath("$.exercises[0].sets").isArray
            .jsonPath("$.exercises[0].sets[0].repetitions").isEqualTo(10)
            .jsonPath("$.exercises[0].sets[0].weight").isEqualTo(20.0f)
            .jsonPath("$.exercises[0].sets[1]").doesNotExist()
            .jsonPath("$.exercises[1].id").isNotEmpty
            .jsonPath("$.exercises[1].order").isEqualTo(2)
            .jsonPath("$.exercises[1].exercise.name").isEqualTo("Pull Up")
            .jsonPath("$.exercises[1].exercise.isBodyweight").isEqualTo(true)
            .jsonPath("$.exercises[1].sets").isArray
            .jsonPath("$.exercises[1].sets[0].repetitions").isEqualTo(10)
            .jsonPath("$.exercises[1].sets[0].weight").isEqualTo(5.0f)
            .jsonPath("$.exercises[1].sets[1]").doesNotExist()
            .jsonPath("$.exercises[2]").doesNotExist()

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
            .jsonPath("$.exercises[0].order").isEqualTo(1)
            .jsonPath("$.exercises[0].exercise.name").isEqualTo("Dip")
            .jsonPath("$.exercises[0].exercise.isBodyweight").isEqualTo(true)
            .jsonPath("$.exercises[0].sets").isArray
            .jsonPath("$.exercises[0].sets[0].repetitions").isEqualTo(10)
            .jsonPath("$.exercises[0].sets[0].weight").isEqualTo(20.0f)
            .jsonPath("$.exercises[0].sets[1]").doesNotExist()
            .jsonPath("$.exercises[1].id").isNotEmpty
            .jsonPath("$.exercises[1].order").isEqualTo(2)
            .jsonPath("$.exercises[1].exercise.name").isEqualTo("Pull Up")
            .jsonPath("$.exercises[1].exercise.isBodyweight").isEqualTo(true)
            .jsonPath("$.exercises[1].sets").isArray
            .jsonPath("$.exercises[1].sets[0].repetitions").isEqualTo(10)
            .jsonPath("$.exercises[1].sets[0].weight").isEqualTo(5.0f)
            .jsonPath("$.exercises[1].sets[1]").doesNotExist()
            .jsonPath("$.exercises[2]").doesNotExist()
    }

    @Test
    fun updateTraining_deleteExercise() {

        insertTrainingExercises(listOf(
            TrainingExercise("1", 1, Exercise("Dip", true), 63.0f, "2025-01-01".toLocalDate(), listOf(
                TrainingExerciseSet(10, 20.0f)
            )),
            TrainingExercise("2", 2, Exercise("Pull Up", true), 63.0f, "2025-01-01".toLocalDate(), listOf(
                TrainingExerciseSet(10, 5.0f)
            ))
        ))

        val newTrainingDto = TrainingDto(
            "2025-01-01".toLocalDate(),
            63.0f,
            listOf(
                TrainingExerciseDto("1", 1, ExerciseDto("Dip", true), listOf(
                    TrainingExerciseSetDto(10, 20.0f)
                ))
            )
        )

        webTestClient()
            .post()
            .uri("/api/trainings")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(newTrainingDto)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.date").isEqualTo("2025-01-01")
            .jsonPath("$.bodyweight").isEqualTo(63.0f)
            .jsonPath("$.exercises").isArray
            .jsonPath("$.exercises[0].id").isEqualTo("1")
            .jsonPath("$.exercises[0].order").isEqualTo(1)
            .jsonPath("$.exercises[0].exercise.name").isEqualTo("Dip")
            .jsonPath("$.exercises[0].exercise.isBodyweight").isEqualTo(true)
            .jsonPath("$.exercises[0].sets").isArray
            .jsonPath("$.exercises[0].sets[0].repetitions").isEqualTo(10)
            .jsonPath("$.exercises[0].sets[0].weight").isEqualTo(20.0f)
            .jsonPath("$.exercises[0].sets[1]").doesNotExist()
            .jsonPath("$.exercises[1]").doesNotExist()

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
            .jsonPath("$.exercises[0].order").isEqualTo(1)
            .jsonPath("$.exercises[0].exercise.name").isEqualTo("Dip")
            .jsonPath("$.exercises[0].exercise.isBodyweight").isEqualTo(true)
            .jsonPath("$.exercises[0].sets").isArray
            .jsonPath("$.exercises[0].sets[0].repetitions").isEqualTo(10)
            .jsonPath("$.exercises[0].sets[0].weight").isEqualTo(20.0f)
            .jsonPath("$.exercises[0].sets[1]").doesNotExist()
            .jsonPath("$.exercises[1]").doesNotExist()
    }

    @Test
    fun deleteTraining() {

        insertTrainingExercises(listOf(
            TrainingExercise("1", 1, Exercise("Dip", true), 63.0f, "2025-01-01".toLocalDate(), listOf(
                TrainingExerciseSet(10, 20.0f)
            )),
            TrainingExercise("2", 2, Exercise("Pull Up", true), 63.0f, "2025-01-01".toLocalDate(), listOf(
                TrainingExerciseSet(10, 5.0f)
            ))
        ))

        webTestClient()
            .delete()
            .uri("/api/trainings/2025-01-01")
            .exchange()
            .expectStatus().isNoContent
            .expectBody().isEmpty

        webTestClient()
            .get()
            .uri("/api/trainings/2025-01-01")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNotFound
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.timestamp").isNotEmpty
            .jsonPath("$.path").isEqualTo("/api/trainings/2025-01-01")
            .jsonPath("$.status").isEqualTo(404)
            .jsonPath("$.error").isNotEmpty
            .jsonPath("$.requestId").isNotEmpty
    }
}