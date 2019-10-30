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
}

val javkartaMailVersion = "1.6.4"
val kotlinxCoroutinesVersion = "1.3.2"
val logbackVersion = "1.2.3"

dependencies {
	implementation(kotlin("stdlib"))
	implementation(kotlin("stdlib-jdk8"))
	implementation("com.microsoft.sqlserver", "mssql-jdbc", "7.4.1.jre11")
	implementation("com.sun.mail", "jakarta.mail", javkartaMailVersion)
	implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-jdk8", kotlinxCoroutinesVersion)

	runtime("ch.qos.logback", "logback-classic", logbackVersion)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
	kotlinOptions.jvmTarget = "1.11"
}

val run by tasks.getting(JavaExec::class) {
	standardInput = System.`in`
}
