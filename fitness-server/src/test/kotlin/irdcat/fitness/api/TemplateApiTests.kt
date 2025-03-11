package irdcat.fitness.api

import irdcat.fitness.service.Exercise
import irdcat.fitness.service.ExerciseDto
import irdcat.fitness.service.TrainingTemplate
import irdcat.fitness.service.TrainingTemplateDto
import irdcat.fitness.service.TrainingTemplateExercise
import irdcat.fitness.service.TrainingTemplateExerciseDto
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.ContentDisposition
import org.springframework.http.ContentDisposition.attachment
import org.springframework.http.MediaType
import org.springframework.http.client.MultipartBodyBuilder
import org.springframework.web.reactive.function.BodyInserters
import kotlin.test.assertEquals

class TemplateApiTests: AbstractApiTest() {

    @BeforeEach
    fun beforeEach() {
        cleanupTrainingTemplates()
    }

    @Test
    fun getTemplates_ok() {

        insertTrainingTemplates(listOf(
            TrainingTemplate("1", "Lower", "Upper/Lower", "Lower day of Upper/Lower workout", listOf(
                TrainingTemplateExercise(Exercise("Squat", false), 4),
                TrainingTemplateExercise(Exercise("Romanian Deadlift", false), 4),
                TrainingTemplateExercise(Exercise("Bulgarian Split Squat", false), 3),
                TrainingTemplateExercise(Exercise("Hip Thrust (Machine)", false), 3),
                TrainingTemplateExercise(Exercise("Calf Raises (Machine)", false), 3)
            )),
            TrainingTemplate("2", "Upper", "Upper/Lower", "Upper day of Upper/Lower workout", listOf(
                TrainingTemplateExercise(Exercise("Dip", true), 4),
                TrainingTemplateExercise(Exercise("Pull Up", true), 4),
                TrainingTemplateExercise(Exercise("Overhead Press", false), 4),
                TrainingTemplateExercise(Exercise("Incline Chest Press (Machine)", false), 3),
                TrainingTemplateExercise(Exercise("Seated Row (Machine)", false), 3)
            ))
        ))

        webTestClient()
            .get()
            .uri("/api/templates")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.[0].id").isEqualTo("1")
            .jsonPath("$.[0].name").isEqualTo("Lower")
            .jsonPath("$.[0].group").isEqualTo("Upper/Lower")
            .jsonPath("$.[0].description").isEqualTo("Lower day of Upper/Lower workout")
            .jsonPath("$.[0].exercises").isArray
            .jsonPath("$.[0].exercises[0].exercise.name").isEqualTo("Squat")
            .jsonPath("$.[0].exercises[0].exercise.isBodyweight").isEqualTo(false)
            .jsonPath("$.[0].exercises[0].setCount").isEqualTo(4)
            .jsonPath("$.[0].exercises[1].exercise.name").isEqualTo("Romanian Deadlift")
            .jsonPath("$.[0].exercises[1].exercise.isBodyweight").isEqualTo(false)
            .jsonPath("$.[0].exercises[1].setCount").isEqualTo(4)
            .jsonPath("$.[0].exercises[2].exercise.name").isEqualTo("Bulgarian Split Squat")
            .jsonPath("$.[0].exercises[2].exercise.isBodyweight").isEqualTo(false)
            .jsonPath("$.[0].exercises[2].setCount").isEqualTo(3)
            .jsonPath("$.[0].exercises[3].exercise.name").isEqualTo("Hip Thrust (Machine)")
            .jsonPath("$.[0].exercises[3].exercise.isBodyweight").isEqualTo(false)
            .jsonPath("$.[0].exercises[3].setCount").isEqualTo(3)
            .jsonPath("$.[0].exercises[4].exercise.name").isEqualTo("Calf Raises (Machine)")
            .jsonPath("$.[0].exercises[4].exercise.isBodyweight").isEqualTo(false)
            .jsonPath("$.[0].exercises[4].setCount").isEqualTo(3)
            .jsonPath("$.[0].exercises[5]").doesNotExist()
            .jsonPath("$.[1].id").isEqualTo("2")
            .jsonPath("$.[1].name").isEqualTo("Upper")
            .jsonPath("$.[1].group").isEqualTo("Upper/Lower")
            .jsonPath("$.[1].description").isEqualTo("Upper day of Upper/Lower workout")
            .jsonPath("$.[1].exercises").isArray
            .jsonPath("$.[1].exercises[0].exercise.name").isEqualTo("Dip")
            .jsonPath("$.[1].exercises[0].exercise.isBodyweight").isEqualTo(true)
            .jsonPath("$.[1].exercises[0].setCount").isEqualTo(4)
            .jsonPath("$.[1].exercises[1].exercise.name").isEqualTo("Pull Up")
            .jsonPath("$.[1].exercises[1].exercise.isBodyweight").isEqualTo(true)
            .jsonPath("$.[1].exercises[1].setCount").isEqualTo(4)
            .jsonPath("$.[1].exercises[2].exercise.name").isEqualTo("Overhead Press")
            .jsonPath("$.[1].exercises[2].exercise.isBodyweight").isEqualTo(false)
            .jsonPath("$.[1].exercises[2].setCount").isEqualTo(4)
            .jsonPath("$.[1].exercises[3].exercise.name").isEqualTo("Incline Chest Press (Machine)")
            .jsonPath("$.[1].exercises[3].exercise.isBodyweight").isEqualTo(false)
            .jsonPath("$.[1].exercises[3].setCount").isEqualTo(3)
            .jsonPath("$.[1].exercises[4].exercise.name").isEqualTo("Seated Row (Machine)")
            .jsonPath("$.[1].exercises[4].exercise.isBodyweight").isEqualTo(false)
            .jsonPath("$.[1].exercises[4].setCount").isEqualTo(3)
            .jsonPath("$.[1].exercises[5]").doesNotExist()
            .jsonPath("$.[2]").doesNotExist()
    }

    @Test
    fun getTemplateById_ok() {

        insertTrainingTemplates(listOf(
            TrainingTemplate("1", "Lower", "Upper/Lower", "Lower day of Upper/Lower workout", listOf(
                TrainingTemplateExercise(Exercise("Squat", false), 4),
                TrainingTemplateExercise(Exercise("Romanian Deadlift", false), 4),
                TrainingTemplateExercise(Exercise("Bulgarian Split Squat", false), 3),
                TrainingTemplateExercise(Exercise("Hip Thrust (Machine)", false), 3),
                TrainingTemplateExercise(Exercise("Calf Raises (Machine)", false), 3)
            )),
            TrainingTemplate("2", "Upper", "Upper/Lower", "Upper day of Upper/Lower workout", listOf(
                TrainingTemplateExercise(Exercise("Dip", true), 4),
                TrainingTemplateExercise(Exercise("Pull Up", true), 4),
                TrainingTemplateExercise(Exercise("Overhead Press", false), 4),
                TrainingTemplateExercise(Exercise("Incline Chest Press (Machine)", false), 3),
                TrainingTemplateExercise(Exercise("Seated Row (Machine)", false), 3)
            ))
        ))

        webTestClient()
            .get()
            .uri("/api/templates/1")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id").isEqualTo("1")
            .jsonPath("$.name").isEqualTo("Lower")
            .jsonPath("$.group").isEqualTo("Upper/Lower")
            .jsonPath("$.description").isEqualTo("Lower day of Upper/Lower workout")
            .jsonPath("$.exercises").isArray
            .jsonPath("$.exercises[0].exercise.name").isEqualTo("Squat")
            .jsonPath("$.exercises[0].exercise.isBodyweight").isEqualTo(false)
            .jsonPath("$.exercises[0].setCount").isEqualTo(4)
            .jsonPath("$.exercises[1].exercise.name").isEqualTo("Romanian Deadlift")
            .jsonPath("$.exercises[1].exercise.isBodyweight").isEqualTo(false)
            .jsonPath("$.exercises[1].setCount").isEqualTo(4)
            .jsonPath("$.exercises[2].exercise.name").isEqualTo("Bulgarian Split Squat")
            .jsonPath("$.exercises[2].exercise.isBodyweight").isEqualTo(false)
            .jsonPath("$.exercises[2].setCount").isEqualTo(3)
            .jsonPath("$.exercises[3].exercise.name").isEqualTo("Hip Thrust (Machine)")
            .jsonPath("$.exercises[3].exercise.isBodyweight").isEqualTo(false)
            .jsonPath("$.exercises[3].setCount").isEqualTo(3)
            .jsonPath("$.exercises[4].exercise.name").isEqualTo("Calf Raises (Machine)")
            .jsonPath("$.exercises[4].exercise.isBodyweight").isEqualTo(false)
            .jsonPath("$.exercises[4].setCount").isEqualTo(3)
            .jsonPath("$.exercises[5]").doesNotExist()
    }

    @Test
    fun getTemplateById_notFound() {

        insertTrainingTemplates(listOf(
            TrainingTemplate("1", "Lower", "Upper/Lower", "Lower day of Upper/Lower workout", listOf(
                TrainingTemplateExercise(Exercise("Squat", false), 4),
                TrainingTemplateExercise(Exercise("Romanian Deadlift", false), 4),
                TrainingTemplateExercise(Exercise("Bulgarian Split Squat", false), 3),
                TrainingTemplateExercise(Exercise("Hip Thrust (Machine)", false), 3),
                TrainingTemplateExercise(Exercise("Calf Raises (Machine)", false), 3)
            )),
            TrainingTemplate("2", "Upper", "Upper/Lower", "Upper day of Upper/Lower workout", listOf(
                TrainingTemplateExercise(Exercise("Dip", true), 4),
                TrainingTemplateExercise(Exercise("Pull Up", true), 4),
                TrainingTemplateExercise(Exercise("Overhead Press", false), 4),
                TrainingTemplateExercise(Exercise("Incline Chest Press (Machine)", false), 3),
                TrainingTemplateExercise(Exercise("Seated Row (Machine)", false), 3)
            ))
        ))

        webTestClient()
            .get()
            .uri("/api/templates/3")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectStatus().isNotFound
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
    }

    @Test
    fun createTemplate_ok() {

        val template = TrainingTemplateDto("", "FWB", "FWB", "Description", listOf(
            TrainingTemplateExerciseDto(ExerciseDto("Squat", false), 5)
        ))

        webTestClient()
            .post()
            .uri("/api/templates")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(template)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id").isNotEmpty
            .jsonPath("$.name").isEqualTo("FWB")
            .jsonPath("$.group").isEqualTo("FWB")
            .jsonPath("$.description").isEqualTo("Description")
            .jsonPath("$.exercises").isArray
            .jsonPath("$.exercises[0].exercise.name").isEqualTo("Squat")
            .jsonPath("$.exercises[0].exercise.isBodyweight").isEqualTo(false)
            .jsonPath("$.exercises[0].setCount").isEqualTo(5)
            .jsonPath("$.exercises[1]").doesNotExist()
    }

    @Test
    fun updateTemplate_ok() {

        insertTrainingTemplates(listOf(
            TrainingTemplate("1", "Lower", "Upper/Lower", "Lower day of Upper/Lower workout", listOf(
                TrainingTemplateExercise(Exercise("Squat", false), 4),
                TrainingTemplateExercise(Exercise("Romanian Deadlift", false), 4),
                TrainingTemplateExercise(Exercise("Bulgarian Split Squat", false), 3),
                TrainingTemplateExercise(Exercise("Hip Thrust (Machine)", false), 3),
                TrainingTemplateExercise(Exercise("Calf Raises (Machine)", false), 3)
            ))
        ))

        val template = TrainingTemplateDto("1", "FWB", "FWB", "Description", listOf(
            TrainingTemplateExerciseDto(ExerciseDto("Squat", false), 5)
        ))

        webTestClient()
            .put()
            .uri("/api/templates/1")
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON)
            .bodyValue(template)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("$.id").isEqualTo("1")
            .jsonPath("$.name").isEqualTo("FWB")
            .jsonPath("$.group").isEqualTo("FWB")
            .jsonPath("$.description").isEqualTo("Description")
            .jsonPath("$.exercises").isArray
            .jsonPath("$.exercises[0].exercise.name").isEqualTo("Squat")
            .jsonPath("$.exercises[0].exercise.isBodyweight").isEqualTo(false)
            .jsonPath("$.exercises[0].setCount").isEqualTo(5)
            .jsonPath("$.exercises[1]").doesNotExist()
    }

    @Test
    fun deleteTemplate_ok() {

        insertTrainingTemplates(listOf(
            TrainingTemplate("1", "Lower", "Upper/Lower", "Lower day of Upper/Lower workout", listOf(
                TrainingTemplateExercise(Exercise("Squat", false), 4),
                TrainingTemplateExercise(Exercise("Romanian Deadlift", false), 4),
                TrainingTemplateExercise(Exercise("Bulgarian Split Squat", false), 3),
                TrainingTemplateExercise(Exercise("Hip Thrust (Machine)", false), 3),
                TrainingTemplateExercise(Exercise("Calf Raises (Machine)", false), 3)
            ))
        ))

        webTestClient()
            .delete()
            .uri("/api/templates/1")
            .exchange()
            .expectStatus().isNoContent
            .expectBody().isEmpty

        assertCollectionIsEmpty(TrainingTemplateExercise::class.java)
    }

    @Test
    fun exportToYaml() {
        insertTrainingTemplates(listOf(
            TrainingTemplate("1", "Lower", "Upper/Lower", "Lower day", listOf(
                TrainingTemplateExercise(Exercise("Squat", false), 5)
            ))
        ))

        val result = webTestClient()
            .get()
            .uri("/api/templates/export/yaml")
            .accept(MediaType.APPLICATION_OCTET_STREAM)
            .exchange()
            .expectHeader().contentType(MediaType.APPLICATION_OCTET_STREAM)
            .expectHeader().contentDisposition(attachment().filename("templates.yaml").build())
            .expectBody()
            .returnResult()
            .responseBodyContent!!
            .toString(Charsets.UTF_8)

        assertEquals(result, """
            - id: "1"
              name: "Lower"
              group: "Upper/Lower"
              description: "Lower day"
              exercises:
              - exercise:
                  name: "Squat"
                  isBodyweight: false
                setCount: 5
            
        """.trimIndent())
    }

    @Test
    fun exportToJson() {
        insertTrainingTemplates(listOf(
            TrainingTemplate("1", "Lower", "Upper/Lower", "Lower day", listOf(
                TrainingTemplateExercise(Exercise("Squat", false), 5)
            ))
        ))

        val result = webTestClient()
            .get()
            .uri("/api/templates/export/json")
            .accept(MediaType.APPLICATION_OCTET_STREAM)
            .exchange()
            .expectHeader().contentType(MediaType.APPLICATION_OCTET_STREAM)
            .expectHeader().contentDisposition(attachment().filename("templates.json").build())
            .expectBody()
            .returnResult()
            .responseBodyContent!!
            .toString(Charsets.UTF_8)

        assertEquals(result, """
            [ {
              "id" : "1",
              "name" : "Lower",
              "group" : "Upper/Lower",
              "description" : "Lower day",
              "exercises" : [ {
                "exercise" : {
                  "name" : "Squat",
                  "isBodyweight" : false
                },
                "setCount" : 5
              } ]
            } ]
        """.trimIndent())
    }

    @Test
    fun importFromYaml() {
        val multipartBodyBuilder = MultipartBodyBuilder()
        multipartBodyBuilder
            .part("file", ByteArrayResource("""
                - id: "1"
                  name: "Lower"
                  group: "Upper/Lower"
                  description: "Lower day"
                  exercises:
                  - exercise:
                      name: "Squat"
                      isBodyweight: false
                    setCount: 5
            """.trimIndent().toByteArray()))
            .contentType(MediaType.APPLICATION_YAML)
            .filename("templates.yaml")

        webTestClient()
            .post()
            .uri("/api/templates/import/yaml")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
            .exchange()
            .expectStatus().isNoContent
            .expectBody().isEmpty
    }

    @Test
    fun importFromJson() {
        val multipartBodyBuilder = MultipartBodyBuilder()
        multipartBodyBuilder
            .part("file", ByteArrayResource("""
                [ {
                  "id" : "1",
                  "name" : "Lower",
                  "group" : "Upper/Lower",
                  "description" : "Lower day",
                  "exercises" : [ {
                    "exercise" : {
                      "name" : "Squat",
                      "isBodyweight" : false
                    },
                    "setCount" : 5
                  } ]
                } ]
            """.trimIndent().toByteArray()))
            .contentType(MediaType.APPLICATION_JSON)
            .filename("templates.json")

        webTestClient()
            .post()
            .uri("/api/templates/import/json")
            .contentType(MediaType.MULTIPART_FORM_DATA)
            .body(BodyInserters.fromMultipartData(multipartBodyBuilder.build()))
            .exchange()
            .expectStatus().isNoContent
            .expectBody().isEmpty
    }
}