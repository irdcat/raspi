plugins {
	kotlin("jvm") version "2.1.21"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.5.0"
	id("io.spring.dependency-management") version "1.1.7"
	id("org.jetbrains.kotlinx.kover") version "0.9.1"
	id("io.gitlab.arturbosch.detekt") version "1.23.8"
}

group = "irdcat"
version = "0.0.1-SNAPSHOT"

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

repositories {
	mavenCentral()
}

extra["springCloudVersion"] = "2025.0.0"

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
	implementation("org.springframework.cloud:spring-cloud-starter-gateway-server-webflux")
	implementation("org.springdoc:springdoc-openapi-starter-webflux-api:2.8.9")
	implementation("org.springdoc:springdoc-openapi-starter-webflux-ui:2.8.8")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.projectreactor:reactor-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test")
	testImplementation("io.rest-assured:rest-assured")
	testImplementation("org.wiremock.integrations:wiremock-spring-boot:3.10.0")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

dependencyManagement {
	imports {
		mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
	}
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

kover {
	reports {
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
	rename(bootJarOutput.name, "api-gateway.jar")
	into(project.layout.buildDirectory.dir("dockerContext"))
}
