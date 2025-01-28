plugins {
	kotlin("jvm") version "2.1.0"
	kotlin("plugin.spring") version "2.1.10"
	id("org.springframework.boot") version "3.4.0"
	id("io.spring.dependency-management") version "1.1.6"
	id("org.jetbrains.kotlinx.kover") version "0.9.1"
}

group = "irdcat"
version = "0.0.1-SNAPSHOT"

repositories {
	mavenCentral()
}

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
	implementation("org.springdoc:springdoc-openapi-starter-webflux-api:2.7.0")
	implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:2.7.0")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.boot:spring-boot-testcontainers")
	testImplementation("io.projectreactor:reactor-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("org.testcontainers:junit-jupiter")
	testImplementation("org.testcontainers:mongodb")
	testImplementation("io.rest-assured:rest-assured")
	testImplementation("jakarta.servlet:jakarta.servlet-api")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
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
	rename(bootJarOutput.name, "fitness.jar")
	into(project.layout.buildDirectory.dir("dockerContext"))
}