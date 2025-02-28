rootProject.name = "fitness-server"

pluginManagement {
    val jvmKotlinVersion: String by settings
    val springKotlinVersion: String by settings
    val springBootVersion: String by settings
    val springDependencyMgmtVersion: String by settings
    val koverVersion: String by settings

    plugins {
        kotlin("jvm") version jvmKotlinVersion
        kotlin("plugin.spring") version springKotlinVersion
        id("org.springframework.boot") version springBootVersion
        id("io.spring.dependency-management") version springDependencyMgmtVersion
        id("org.jetbrains.kotlinx.kover") version koverVersion
    }
}