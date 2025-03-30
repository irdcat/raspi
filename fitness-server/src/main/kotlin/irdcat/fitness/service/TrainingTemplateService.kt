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
import org.slf4j.LoggerFactory
import org.springframework.core.io.Resource
import org.springframework.data.mongodb.core.FindAndReplaceOptions
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.http.codec.multipart.FilePart
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import reactor.kotlin.core.publisher.toMono
import reactor.util.function.Tuples
import java.util.UUID

@Service
class TrainingTemplateService(
    private val reactiveMongoTemplate: ReactiveMongoTemplate
) {

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
        const val ID = "_id"

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

        return reactiveMongoTemplate
            .findAll(TrainingTemplate::class.java)
            .map(TrainingTemplateDto::fromTrainingTemplate)
    }

    fun findTemplateById(id: String): Mono<TrainingTemplateDto> {

        return id.toMono()
            .map { Query().addCriteria(where(ID).isEqualTo(it)) }
            .flatMap { reactiveMongoTemplate.findOne(it, TrainingTemplate::class.java) }
            .map(TrainingTemplateDto::fromTrainingTemplate)
            .switchIfEmpty(TrainingTemplateNotFoundException("Training Template with id ${id} not found").toMono())
    }

    fun createTemplate(trainingTemplate: TrainingTemplateDto): Mono<TrainingTemplateDto> {

        return trainingTemplate.toMono()
            .map(TrainingTemplateDto::toTrainingTemplate)
            .map { it.copy(id = UUID.randomUUID().toString()) }
            .flatMap { reactiveMongoTemplate.insert(it) }
            .doOnNext { logger.debug("Added {}", it) }
            .map(TrainingTemplateDto::fromTrainingTemplate)
    }

    fun updateTemplate(id: String, trainingTemplate: TrainingTemplateDto): Mono<TrainingTemplateDto> {

        return Tuples.of(id, trainingTemplate)
            .toMono()
            .map {
                Tuples.of(
                    Query().addCriteria(where(ID).isEqualTo(it.t1)),
                    trainingTemplate.toTrainingTemplate().copy(id = null)
                )
            }
            .flatMap {
                reactiveMongoTemplate.findAndReplace(
                    it.t1, it.t2, FindAndReplaceOptions.options().upsert().returnNew())
            }
            .doOnNext { logger.debug("Updated {}", it) }
            .map(TrainingTemplateDto::fromTrainingTemplate)
    }

    fun deleteTemplate(id: String): Mono<Void> {

        return id.toMono()
            .map { Query().addCriteria(where(ID).isEqualTo(it)) }
            .flatMap { reactiveMongoTemplate.remove(it, TrainingTemplate::class.java) }
            .then()
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
        return map { it.copy(id = UUID.randomUUID().toString()) }
            .collectList()
            .flatMapMany { reactiveMongoTemplate.insert(it, TrainingTemplate::class.java) }
            .doOnNext { logger.debug("Imported training template: {}", it) }
            .then()
    }
}