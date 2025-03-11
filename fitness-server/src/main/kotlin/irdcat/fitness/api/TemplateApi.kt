package irdcat.fitness.api

import irdcat.fitness.exception.TrainingTemplateNotFoundException
import irdcat.fitness.service.TrainingTemplateDto
import irdcat.fitness.service.TrainingTemplateService
import org.slf4j.LoggerFactory
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.http.codec.multipart.FilePart
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.http.server.reactive.ServerHttpResponse
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.ResponseBody
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono

@RestController
@RequestMapping("/api/templates")
class TemplateApi(
    private val trainingTemplateService: TrainingTemplateService
) {

    companion object {
        private val logger = LoggerFactory.getLogger(this::class.java)
    }

    @GetMapping(produces = [APPLICATION_JSON_VALUE])
    fun findTemplates(): Flux<TrainingTemplateDto> {
        logger.info("Requested all templates")
        return trainingTemplateService.findAllTemplates()
    }

    @GetMapping("/{id}", produces = [APPLICATION_JSON_VALUE])
    fun findTemplateById(@PathVariable id: String): Mono<TrainingTemplateDto> {
        logger.info("Requested template with id {}", id)
        return trainingTemplateService.findTemplateById(id)
    }

    @PostMapping(produces = [APPLICATION_JSON_VALUE], consumes = [APPLICATION_JSON_VALUE])
    fun createTemplate(@RequestBody trainingTemplateDto: TrainingTemplateDto): Mono<TrainingTemplateDto> {
        logger.info("Creating template {}", trainingTemplateDto)
        return trainingTemplateService.createTemplate(trainingTemplateDto)
    }

    @PutMapping("/{id}", produces = [APPLICATION_JSON_VALUE], consumes = [APPLICATION_JSON_VALUE])
    fun updateTemplate(@PathVariable id: String, @RequestBody trainingTemplateDto: TrainingTemplateDto): Mono<TrainingTemplateDto> {
        logger.info("Updating template with id {} to {}", id, trainingTemplateDto)
        return trainingTemplateService.updateTemplate(id, trainingTemplateDto)
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun deleteTemplate(@PathVariable id: String): Mono<Void> {
        logger.info("Deleting template with id {}", id)
        return trainingTemplateService.deleteTemplate(id)
    }

    @GetMapping("/export/yaml")
    @ResponseBody
    fun exportYaml(serverHttpResponse: ServerHttpResponse): Mono<Resource> {
        serverHttpResponse.apply {
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=templates.yaml")
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
        }
        return trainingTemplateService.exportToYaml()
    }

    @GetMapping("/export/json")
    @ResponseBody
    fun exportJson(serverHttpResponse: ServerHttpResponse): Mono<Resource> {
        serverHttpResponse.apply {
            headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=templates.json")
            headers.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
        }
        return trainingTemplateService.exportToJson()
    }

    @PostMapping("/import/yaml", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun importYaml(@RequestPart("file", required = true) file: Mono<FilePart>): Mono<Void> {
        return file.flatMap(trainingTemplateService::importFromYaml)
    }

    @PostMapping("/import/json", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    @ResponseStatus(HttpStatus.NO_CONTENT)
    fun exportJson(@RequestPart("file", required = true) file: Mono<FilePart>): Mono<Void> {
        return file.flatMap(trainingTemplateService::importFromJson)
    }

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(TrainingTemplateNotFoundException::class)
    fun templateNotFound(
        exception: TrainingTemplateNotFoundException,
        serverHttpRequest: ServerHttpRequest): Mono<ErrorResponse> {

        return ErrorResponse
            .fromThrowable(exception, serverHttpRequest, HttpStatus.NOT_FOUND.value())
            .toMono()
    }
}