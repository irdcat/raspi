package irdcat.fitness.api

import irdcat.fitness.service.CountedExerciseDto
import irdcat.fitness.service.ExerciseService
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux

@RestController
@RequestMapping("/api/exercises", produces = [APPLICATION_JSON_VALUE])
class ExerciseApi(
    private val exerciseService: ExerciseService
) {

    @GetMapping("/counted")
    fun findCounted(
        @RequestParam name: String,
        @RequestParam page: Int,
        @RequestParam pageSize: Int): Flux<CountedExerciseDto> {

        return exerciseService.findCountedByName(name, page, pageSize)
    }

    @GetMapping("/names")
    fun findNames(): Flux<String> {

        return exerciseService.findAllNames()
    }
}