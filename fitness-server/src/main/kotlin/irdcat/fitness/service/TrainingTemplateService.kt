package irdcat.fitness.service

import irdcat.fitness.exception.TrainingTemplateNotFoundException
import org.springframework.data.mongodb.core.FindAndReplaceOptions
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.ReplaceOptions
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import reactor.util.function.Tuples
import java.util.UUID

@Service
class TrainingTemplateService(
    private val reactiveMongoTemplate: ReactiveMongoTemplate
) {

    companion object {
        const val ID = "_id"
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
            .map(TrainingTemplateDto::fromTrainingTemplate)
    }

    fun deleteTemplate(id: String): Mono<Void> {

        return id.toMono()
            .map { Query().addCriteria(where(ID).isEqualTo(it)) }
            .flatMap { reactiveMongoTemplate.remove(it, TrainingTemplate::class.java) }
            .then()
    }
}