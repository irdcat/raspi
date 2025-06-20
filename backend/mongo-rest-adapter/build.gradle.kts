plugins {
	kotlin("jvm") version "2.1.21"
	kotlin("plugin.spring") version "2.1.21"
	id("org.springframework.boot") version "3.5.2"
	id("io.spring.dependency-management") version "1.1.7"
	id("org.jetbrains.kotlinx.kover") version "0.9.1"
	id("io.gitlab.arturbosch.detekt") version "1.23.8"
}

group = "irdcat"
version = "0.0.1-SNAPSHOT"

repositories {
	mavenCentral()
}

configurations.matching { it.name == "detekt" }.all {
	resolutionStrategy.eachDependency {
		if (requested.group == "org.jetbrains.kotlin") {
			useVersion(io.gitlab.arturbosch.detekt.getSupportedKotlinVersion())
		}
	}
}

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
	implementation("org.springdoc:springdoc-openapi-starter-webflux-api:2.8.9")
	implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:2.8.9")
	implementation(platform("org.mongodb:mongodb-driver-bom:5.5.1"))
	implementation("org.mongodb:mongodb-driver-reactivestreams")
	implementation("de.undercouch:bson4jackson:2.18.0")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.projectreactor:reactor-test")
	testImplementation("org.testcontainers:mongodb")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll(
			"-Xjsr305=strict"
		)
	}
}

kover {
	reports {
		filters {
			excludes {
				classes("irdcat.fitness.FitnessApplicationKt")
			}
		}
		verify {
			rule {
				minBound(85)
			}
		}
	}
}

detekt {
	config.setFrom(files("detekt.yaml"))
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.register<Task>("runLocal") {
	dependsOn(tasks.bootTestRun)
}

tasks.register<Copy>("buildDockerContext") {
	dependsOn(tasks.bootJar)

	val bootJarOutput = tasks.bootJar.get().outputs.files.singleFile

	from("Dockerfile")
	from(bootJarOutput)
	rename(bootJarOutput.name, "mongo-rest-adapter.jar")
	into(project.layout.buildDirectory.dir("dockerContext"))
}
