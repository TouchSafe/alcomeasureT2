plugins {
	`kotlin-dsl`
	`maven-publish`
	application
	id("com.github.johnrengelman.shadow") version "5.1.0"
}

group = "au.com.touchsafe"
version = "1.0.0-SNAPSHOT"
description = "TouchSafe 2 AlcoMeasure Integration."

application {
	mainClassName = "au.com.touchsafe.alcomeasure.ApplicationKt"
}

repositories {
	jcenter()
	mavenCentral()
	maven { url = uri("https://repo.spring.io/plugins-release/") }
}

val kotlinxCoroutinesReactorVersion = "1.3.2"
val logbackVersion = "1.2.3"
val r2dbcMssqlVersion = "0.8.0.M8"

dependencies {
	implementation(kotlin("stdlib"))
	implementation("io.r2dbc", "r2dbc-mssql", r2dbcMssqlVersion)
	implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-jdk8",kotlinxCoroutinesReactorVersion)
	implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-reactor", kotlinxCoroutinesReactorVersion)

	runtime("ch.qos.logback", "logback-classic", logbackVersion)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
	kotlinOptions.jvmTarget = "1.11"
}

// TODO: Remove once console testing has been completed:
val run by tasks.getting(JavaExec::class) {
	standardInput = System.`in`
}
