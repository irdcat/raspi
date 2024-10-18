package irdcat.fitness

import org.springframework.boot.fromApplication
import org.springframework.boot.with

fun main(args: Array<String>) {
	
	fromApplication<FitnessApplication>()
		.with(TestContainersConfiguration::class)
		.run(*args)
}
