package irdcat.fitness

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FitnessApplication

@Suppress("SpreadOperator")
fun main(args: Array<String>) {
	runApplication<FitnessApplication>(*args)
}
