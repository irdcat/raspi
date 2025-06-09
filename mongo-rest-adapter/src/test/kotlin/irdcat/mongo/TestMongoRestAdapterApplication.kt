package irdcat.mongo

import org.springframework.boot.fromApplication
import org.springframework.boot.with

fun main(args: Array<String>) {
    fromApplication<MongoRestAdapterApplication>()
        .with(TestContainersConfiguration::class)
        .run(*args)
}