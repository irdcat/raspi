import com.github.gradle.node.npm.task.NpmTask

plugins {
    kotlin("jvm") version "1.9.25"
    id("com.github.node-gradle.node") version "7.1.0"
}

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

node {
    version = "20.15.1"
    download = true
}

tasks.register<NpmTask>("npmRunDev") {
    dependsOn(tasks.npmInstall)
    args.set(listOf("run", "dev"))
}

tasks.register<NpmTask>("npmRunBuild") {
    dependsOn(tasks.npmInstall)
    args.set(listOf("run", "build"))
}

tasks.register<NpmTask>("npmRunPreview") {
    dependsOn(tasks.npmInstall)
    args.set(listOf("run", "preview"))
}