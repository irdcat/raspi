package irdcat.mongo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class MongoRestAdapterApplication

fun main(args: Array<String>) {
	runApplication<MongoRestAdapterApplication>(*args)
}
