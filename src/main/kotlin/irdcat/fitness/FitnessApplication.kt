package irdcat.fitness

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories

@SpringBootApplication
class FitnessApplication

fun main(args: Array<String>) {
	runApplication<FitnessApplication>(*args)
}
