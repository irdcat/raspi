package irdcat.fitness.api

import irdcat.fitness.AbstractIntegrationTest
import irdcat.fitness.model.Exercise
import irdcat.fitness.model.TrainingTemplate
import irdcat.fitness.model.TrainingTemplateDto
import irdcat.fitness.repository.TrainingTemplateRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient

@AutoConfigureWebTestClient
class TrainingTemplateApiTests: AbstractIntegrationTest() {

    private val templates = listOf(
        TrainingTemplate("1", "Push", "PPL", "Description" , listOf("1", "2", "3")),
        TrainingTemplate("2", "Pull", "PPL", "Description", listOf("4", "5", "6")),
        TrainingTemplate("3", "Legs", "PPL", "Description", listOf("7", "8", "9"))
    )

    @Autowired
    private lateinit var trainingTemplateRepository: TrainingTemplateRepository

    @Autowired
    private lateinit var webTestClient: WebTestClient

    @BeforeEach
    fun beforeEach() {
        trainingTemplateRepository.deleteAll().block()
    }

    @Test
    fun getTrainingTemplates_ok() {
        trainingTemplateRepository.insert(templates).blockLast()

        webTestClient
            .get()
            .uri("/api/templates")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.[0].id").isEqualTo("1")
            .jsonPath("$.[0].name").isEqualTo("Push")
            .jsonPath("$.[0].groupName").isEqualTo("PPL")
            .jsonPath("$.[0].description").isEqualTo("Description")
            .jsonPath("$.[0].exerciseIds[0]").isEqualTo("1")
            .jsonPath("$.[0].exerciseIds[1]").isEqualTo("2")
            .jsonPath("$.[0].exerciseIds[2]").isEqualTo("3")
            .jsonPath("$.[1].id").isEqualTo("2")
            .jsonPath("$.[1].name").isEqualTo("Pull")
            .jsonPath("$.[1].groupName").isEqualTo("PPL")
            .jsonPath("$.[1].description").isEqualTo("Description")
            .jsonPath("$.[1].exerciseIds[0]").isEqualTo("4")
            .jsonPath("$.[1].exerciseIds[1]").isEqualTo("5")
            .jsonPath("$.[1].exerciseIds[2]").isEqualTo("6")
            .jsonPath("$.[2].id").isEqualTo("3")
            .jsonPath("$.[2].name").isEqualTo("Legs")
            .jsonPath("$.[2].groupName").isEqualTo("PPL")
            .jsonPath("$.[2].description").isEqualTo("Description")
            .jsonPath("$.[2].exerciseIds[0]").isEqualTo("7")
            .jsonPath("$.[2].exerciseIds[1]").isEqualTo("8")
            .jsonPath("$.[2].exerciseIds[2]").isEqualTo("9")
    }

    @Test
    fun getTrainingTemplates_notFound() {
        webTestClient
            .get()
            .uri("/api/templates")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$").isArray
            .jsonPath("$.[0]").doesNotExist()
    }

    @Test
    fun getTrainingTemplate_ok() {
        trainingTemplateRepository.insert(templates).blockLast()

        webTestClient
            .get()
            .uri("/api/templates/1")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo("1")
            .jsonPath("$.name").isEqualTo("Push")
            .jsonPath("$.groupName").isEqualTo("PPL")
            .jsonPath("$.description").isEqualTo("Description")
            .jsonPath("$.exerciseIds[0]").isEqualTo("1")
            .jsonPath("$.exerciseIds[1]").isEqualTo("2")
            .jsonPath("$.exerciseIds[2]").isEqualTo("3")
    }

    @Test
    fun getTrainingTemplate_notFound() {
        webTestClient
            .get()
            .uri("/api/templates/1")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectStatus().isNotFound
    }

    @Test
    fun addTrainingTemplate_ok() {
        webTestClient
            .post()
            .uri("/api/templates")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TrainingTemplateDto.fromTrainingTemplate(templates[0]))
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo("1")
            .jsonPath("$.name").isEqualTo("Push")
            .jsonPath("$.groupName").isEqualTo("PPL")
            .jsonPath("$.description").isEqualTo("Description")
            .jsonPath("$.exerciseIds[0]").isEqualTo("1")
            .jsonPath("$.exerciseIds[1]").isEqualTo("2")
            .jsonPath("$.exerciseIds[2]").isEqualTo("3")
    }

    @Test
    fun addTrainingTemplate_badRequest() {
        webTestClient
            .post()
            .uri("/api/templates")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TrainingTemplateDto.fromTrainingTemplate(templates[0]).copy(name = null))
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectStatus().isBadRequest
    }

    @Test
    fun updateTrainingTemplate_ok() {
        trainingTemplateRepository.insert(templates).blockLast()

        webTestClient
            .put()
            .uri("/api/templates/1")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TrainingTemplateDto.fromTrainingTemplate(templates[0]).copy(name = "Push me :)"))
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo("1")
            .jsonPath("$.name").isEqualTo("Push me :)")
            .jsonPath("$.groupName").isEqualTo("PPL")
            .jsonPath("$.description").isEqualTo("Description")
            .jsonPath("$.exerciseIds[0]").isEqualTo("1")
            .jsonPath("$.exerciseIds[1]").isEqualTo("2")
            .jsonPath("$.exerciseIds[2]").isEqualTo("3")
    }

    @Test
    fun updateTrainingTemplate_badRequest() {
        trainingTemplateRepository.insert(templates).blockLast()

        webTestClient
            .put()
            .uri("/api/templates/1")
            .contentType(MediaType.APPLICATION_JSON)
            .bodyValue(TrainingTemplateDto.fromTrainingTemplate(templates[0]).copy(name = null))
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectStatus().isBadRequest
    }

    @Test
    fun deleteTrainingTemplate_ok() {
        trainingTemplateRepository.insert(templates).blockLast()

        webTestClient
            .delete()
            .uri("/api/templates/1")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.id").isEqualTo("1")
            .jsonPath("$.name").doesNotExist()
    }
}