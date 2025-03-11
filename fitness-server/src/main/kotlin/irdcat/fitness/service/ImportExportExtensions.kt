package irdcat.fitness.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.core.io.InputStreamResource
import org.springframework.core.io.Resource
import org.springframework.http.codec.multipart.FilePart
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

fun <T> Flux<T>.toResourceMono(objectMapper: ObjectMapper): Mono<Resource> {
    return collectList()
        .map { it.toResource(objectMapper) }
}

private fun <T> List<T>.toResource(objectMapper: ObjectMapper): Resource {
    val outputStream = ByteArrayOutputStream()
    objectMapper.writeValue(outputStream, this)
    outputStream.flush()
    val inputStream = ByteArrayInputStream(outputStream.toByteArray())
    return InputStreamResource(inputStream)
}

fun <T> FilePart.toMappedFlux(objectMapper: ObjectMapper, elementType: Class<T>): Flux<T> {
    val listType = objectMapper.typeFactory.constructCollectionType(ArrayList::class.java, elementType)
    return toMono()
        .flatMapMany(FilePart::content)
        .map { it.asInputStream() }
        .toMono()
        .map { objectMapper.readValue(it, listType) as List<T> }
        .flatMapMany(Flux<T>::fromIterable)
}