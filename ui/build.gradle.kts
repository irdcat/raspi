import com.github.gradle.node.npm.task.NpmTask

plugins {
    id("com.github.node-gradle.node") version "7.1.0"
}

repositories {
    mavenCentral()
}

node {
    version = "20.15.1"
    download = true
}

tasks.register<NpmTask>("npmStart") {
    dependsOn(tasks.npmInstall)
    args.set(listOf("start"))
}

tasks.register<NpmTask>("npmRunBuild") {
    dependsOn(tasks.npmInstall)
    args.set(listOf("run", "build"))
}

tasks.register<Task>("runLocal") {
    dependsOn(tasks.named("npmStart"))
}