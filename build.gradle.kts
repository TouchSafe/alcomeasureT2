plugins {
	kotlin("jvm") version "1.3.72"
	`maven-publish`
	application
	id("com.github.johnrengelman.shadow") version "5.2.0"
}

group = "au.com.touchsafe"
version = "1.0.1-SNAPSHOT"
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

	// Testing
	testImplementation("org.junit.jupiter", "junit-jupiter-api", "5.6.2")
	testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine", "5.6.2")
	testImplementation("com.icegreen", "greenmail", "1.5.14")
	testImplementation("org.hsqldb", "hsqldb", "2.5.1")
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
		exclude("logback.xml", "settings.properties", "settings-deployment.properties")
		rename("logback-deployment.xml", "logback.xml")
		mergeServiceFiles()
		isZip64 = true
	}
}

// https://junit.org/junit5/docs/current/user-guide/#running-tests-build-gradle
tasks.test {
	useJUnitPlatform()
}