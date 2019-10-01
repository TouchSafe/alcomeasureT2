plugins {
	`kotlin-dsl`
	`maven-publish`
	application
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
//val ktorVersion = "1.2.2" // TODO: Upgrading this to "1.2.3" makes it so that we cannot log in to base-test!!! Upgrading this to "1.2.4" causes a compilation error!!!
val r2dbcMssqlVersion = "1.0.0.M7"

dependencies {
	implementation(kotlin("stdlib"))
//	implementation("io.ktor", "ktor-auth", ktorVersion)
//	implementation("io.ktor", "ktor-jackson", ktorVersion)
//	implementation("io.ktor", "ktor-server-netty", ktorVersion)
//	implementation("io.ktor", "ktor-server-sessions", ktorVersion)
	implementation("io.r2dbc", "r2dbc-mssql", r2dbcMssqlVersion)
	implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-reactor", kotlinxCoroutinesReactorVersion)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
	kotlinOptions.jvmTarget = "1.11"
}
