package irdcat.fitness.repository

import irdcat.fitness.service.model.TrainingTemplate
import org.springframework.data.mongodb.core.FindAndReplaceOptions.options
import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.query.Query.query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.stereotype.Repository
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import reactor.util.function.Tuples
import java.util.UUID

@Repository
class TemplateRepository(
    private val reactiveMongoTemplate: ReactiveMongoTemplate
) {

    companion object {
        const val ID = "_id"
    }

    fun findAll(): Flux<TrainingTemplate> {
        return reactiveMongoTemplate.findAll(TrainingTemplate::class.java)
    }

    fun findById(id: String): Mono<TrainingTemplate> {
        return id.toMono()
            .map { query(where(ID).isEqualTo(it)) }
            .flatMap { reactiveMongoTemplate.findOne(it, TrainingTemplate::class.java) }
    }

    fun create(template: TrainingTemplate): Mono<TrainingTemplate> {
        return template.toMono()
            .map { it.copy(id = UUID.randomUUID().toString()) }
            .flatMap { reactiveMongoTemplate.insert(it) }
    }

    fun create(templates: List<TrainingTemplate>): Flux<TrainingTemplate> {
        return templates
            .toFlux()
            .map { it.copy(id = UUID.randomUUID().toString()) }
            .collectList()
            .flatMapMany { reactiveMongoTemplate.insert(it, TrainingTemplate::class.java) }
    }

    fun update(id: String, trainingTemplate: TrainingTemplate): Mono<TrainingTemplate> {
        return Tuples.of(id, trainingTemplate)
            .toMono()
            .map { Tuples.of(query(where(ID).isEqualTo(it.t1)), it.t2.copy(id = null)) }
            .flatMap { reactiveMongoTemplate.findAndReplace(it.t1, it.t2, options().upsert().returnNew()) }
    }

    fun delete(id: String): Mono<Void> {
        return id.toMono()
            .map { query(where(ID).isEqualTo(it)) }
            .flatMap { reactiveMongoTemplate.remove(it, TrainingTemplate::class.java) }
            .then()
    }
}