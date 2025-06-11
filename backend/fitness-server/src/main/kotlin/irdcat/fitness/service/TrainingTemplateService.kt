package irdcat.fitness.service

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonFactory
import com.fasterxml.jackson.databind.SerializationFeature
import com.fasterxml.jackson.databind.json.JsonMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.dataformat.yaml.YAMLGenerator
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import irdcat.fitness.exception.TrainingTemplateNotFoundException
import irdcat.fitness.repository.TemplateRepository
import irdcat.fitness.service.dto.TrainingTemplateDto
import org.slf4j.LoggerFactory
import org.springframework.core.io.Resource
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.kotlin.core.publisher.toMono

@Service
class TrainingTemplateService(
    private val templateRepository: TemplateRepository
) {

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)

        private val yamlFactory = YAMLFactory.builder()
            .disable(YAMLGenerator.Feature.WRITE_DOC_START_MARKER)
            .build()
        private val jsonFactory = JsonFactory.builder()
            .build()
        private val yamlMapper = YAMLMapper(yamlFactory)
            .registerKotlinModule()
            .registerModule(JavaTimeModule())
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
        private val jsonMapper = JsonMapper(jsonFactory)
            .registerKotlinModule()
            .registerModule(JavaTimeModule())
            .enable(SerializationFeature.INDENT_OUTPUT)
            .setSerializationInclusion(JsonInclude.Include.NON_NULL)
    }

    fun findAllTemplates(): Flux<TrainingTemplateDto> {

        return templateRepository
            .findAll()
            .map(TrainingTemplateDto::fromTrainingTemplate)
    }

    fun findTemplateById(id: String): Mono<TrainingTemplateDto> {

        return templateRepository
            .findById(id)
            .map(TrainingTemplateDto::fromTrainingTemplate)
            .switchIfEmpty(TrainingTemplateNotFoundException("Training Template with id ${id} not found").toMono())
    }

    fun createTemplate(trainingTemplate: TrainingTemplateDto): Mono<TrainingTemplateDto> {

        return trainingTemplate.toMono()
            .map(TrainingTemplateDto::toTrainingTemplate)
            .flatMap { templateRepository.create(it) }
            .doOnNext { logger.debug("Added {}", it) }
            .map(TrainingTemplateDto::fromTrainingTemplate)
    }

    fun updateTemplate(id: String, trainingTemplate: TrainingTemplateDto): Mono<TrainingTemplateDto> {

        val template = trainingTemplate.toTrainingTemplate()
        return templateRepository
            .update(id, template)
            .doOnNext { logger.debug("Updated {}", it) }
            .map(TrainingTemplateDto::fromTrainingTemplate)
    }

    fun deleteTemplate(id: String): Mono<Void> {

        return templateRepository.delete(id)
    }

    fun exportToYaml(): Mono<Resource> {
        return findAllTemplates()
            .toResourceMono(yamlMapper)
            .subscribeOn(Schedulers.boundedElastic())
    }

    fun exportToJson(): Mono<Resource> {
        return findAllTemplates()
            .toResourceMono(jsonMapper)
            .subscribeOn(Schedulers.boundedElastic())
    }

    fun importFromYaml(filePart: FilePart): Mono<Void> {
        return filePart
            .toMappedFlux(yamlMapper, TrainingTemplateDto::class.java)
            .import()
            .subscribeOn(Schedulers.boundedElastic())
    }

    fun importFromJson(filePart: FilePart): Mono<Void> {
        return filePart
            .toMappedFlux(jsonMapper, TrainingTemplateDto::class.java)
            .import()
            .subscribeOn(Schedulers.boundedElastic())
    }

    private fun Flux<TrainingTemplateDto>.import(): Mono<Void> {
        return map(TrainingTemplateDto::toTrainingTemplate)
            .collectList()
            .flatMapMany { templateRepository.create(it) }
            .doOnNext { logger.debug("Imported training template: {}", it) }
            .then()
    }
}