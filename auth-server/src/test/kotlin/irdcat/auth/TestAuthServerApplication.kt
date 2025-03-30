package irdcat.auth

import org.springframework.boot.fromApplication
import org.springframework.boot.with

fun main(args: Array<String>) {
	fromApplication<AuthServerApplication>()
		.with(TestcontainersConfiguration::class)
		.run(*args)
}
