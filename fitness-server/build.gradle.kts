plugins {
	kotlin("jvm")
	kotlin("plugin.spring")
	id("org.springframework.boot")
	id("io.spring.dependency-management")
	id("org.jetbrains.kotlinx.kover")
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

val springDocVersion: String by project

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
	implementation("org.springdoc:springdoc-openapi-starter-webflux-api:${springDocVersion}")
	implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:${springDocVersion}")
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