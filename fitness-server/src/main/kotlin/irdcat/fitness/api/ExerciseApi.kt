package irdcat.fitness.api

import irdcat.fitness.Constants.RequestParameters
import irdcat.fitness.service.CountedExerciseDto
import irdcat.fitness.service.ExerciseService
import irdcat.fitness.service.Page
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/exercises", produces = [APPLICATION_JSON_VALUE])
class ExerciseApi(
    private val exerciseService: ExerciseService
) {

    @GetMapping("/counted")
    fun findCounted(
        @RequestParam name: String?,
        @RequestParam(RequestParameters.PAGE, defaultValue = "0") page: Long,
        @RequestParam(RequestParameters.SIZE, defaultValue = "20") pageSize: Long): Mono<Page<CountedExerciseDto>> {

        return exerciseService.findCountedByName(name, page, pageSize)
    }

    @GetMapping("/names")
    fun findNames(): Mono<List<String>> {

        return exerciseService.findAllNames()
    }
}