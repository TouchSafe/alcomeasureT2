plugins {
	kotlin("jvm") version "1.4.0"
	`maven-publish`
	application
	id("com.github.johnrengelman.shadow") version "5.2.0"
	id("org.jetbrains.dokka") version "1.4.0-rc"
}

group = "au.com.touchsafe"
version = "1.0.6-SNAPSHOT"
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
	implementation("io.lettuce" ,"lettuce-core" ,"5.3.3.RELEASE")
	implementation("org.jetbrains.kotlinx" ,"kotlinx-coroutines-core" ,"1.3.9")
	implementation("com.github.kstyrc" ,"embedded-redis" ,"0.6")
	implementation("org.apache.commons" ,"commons-lang3" ,"3.11")
	implementation("com.1stleg" ,"jnativehook" ,"2.1.0")
	// implementation("com.github.kwhat", "jnativehook", "2.2-SNAPSHOT")

	// Testing
	testImplementation("org.junit.jupiter", "junit-jupiter-api", "5.6.2")
	testRuntimeOnly("org.junit.jupiter", "junit-jupiter-engine", "5.6.2")
	testImplementation("com.icegreen", "greenmail", "1.5.14")
	testImplementation("org.hsqldb", "hsqldb", "2.5.1")
	testImplementation("io.lettuce" ,"lettuce-core" ,"5.3.3.RELEASE")
	testImplementation("org.jetbrains.kotlinx" ,"kotlinx-coroutines-core" ,"1.3.9")
	testImplementation("com.github.kstyrc" ,"embedded-redis" ,"0.6")
	testImplementation("com.1stleg" ,"jnativehook" ,"2.1.0")
	testImplementation("org.apache.commons" ,"commons-lang3" ,"3.11")
	// testImplementation("com.github.kwhat", "jnativehook", "2.2-SNAPSHOT")

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