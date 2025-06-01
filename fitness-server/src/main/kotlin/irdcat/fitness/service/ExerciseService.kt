package irdcat.fitness.service

import irdcat.fitness.repository.ExerciseRepository
import irdcat.fitness.repository.model.ValueHolder
import irdcat.fitness.service.dto.CountedExerciseDto
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@Service
class ExerciseService(
    private val exerciseRepository: ExerciseRepository
) {
    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
    }

    fun findAllNames(): Mono<List<String>> {
        return exerciseRepository
            .findDistinctNames()
            .collectList()
            .doOnNext { logger.debug("Exercise names: {}", it) }
    }

    fun findCountedByName(
        name: String?, page: Long, pageSize: Long): Mono<Page<CountedExerciseDto>> {

        val resultsMono = exerciseRepository
            .findCountedByName(name, page * pageSize, pageSize)
            .collectList()

        val countMono = exerciseRepository
            .countByName(name)

        return resultsMono
            .zipWith(countMono)
            .map { Page(it.t1, page, pageSize, it.t2.value) }
            .switchIfEmpty(Page(listOf<CountedExerciseDto>(), page, pageSize, 0).toMono())
            .doOnNext {
                logger.debug(
                    "CountedExercise Page: [page={}, size={}, totalResults={}]",
                    it.currentPage, it.pageSize, it.totalResults)
            }
    }
}
