plugins {
	kotlin("jvm") version "1.3.72"
	`maven-publish`
	application
	id("com.github.johnrengelman.shadow") version "5.2.0"
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

dependencies {
	implementation(kotlin("stdlib"))
	implementation(kotlin("stdlib-jdk8"))
	implementation("ch.qos.logback", "logback-classic", "1.2.3")
	implementation("com.microsoft.sqlserver", "mssql-jdbc", "7.4.1.jre11")
	implementation("lc.kra.system", "system-hook", "3.7")
	implementation("com.sun.mail", "jakarta.mail", "1.6.4")
	implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-jdk8", "1.3.2")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
	kotlinOptions.jvmTarget = "11"
}

val run by tasks.getting(JavaExec::class) {
	standardInput = System.`in`
}

tasks {
	shadowJar {
		description = "Generate the Application JAR for deployment."
		archiveClassifier.set("")
		destinationDirectory.set(project.buildDir.resolve("distributions"))
		exclude("logback.xml", "settings.properties")
		rename("logback-deployment.xml", "logback.xml")
		rename("settings-deployment.properties", "settings.properties")
		mergeServiceFiles()
		isZip64 = true
	}
}
