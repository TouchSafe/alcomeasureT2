package au.com.touchsafe.alcomeasure.util

import au.com.touchsafe.alcomeasure.ConfigurableConsoleAppender
import au.com.touchsafe.alcomeasure.ConfigurableRollingFileAppender
import au.com.touchsafe.alcomeasure.SETTINGS_PROPERTIES

fun setOutputLoggingLevels() {
	setConsoleLoggingLevel()
	setFileLoggingLevel()
}

fun setConsoleLoggingLevel() {
	val consoleLogLevel = SETTINGS_PROPERTIES.getProperty("consoleLogLevel") ?: return

	val level = ch.qos.logback.classic.Level.toLevel(consoleLogLevel)

	ConfigurableConsoleAppender.level = level
}

fun setFileLoggingLevel() {
	val fileLogLevel = SETTINGS_PROPERTIES.getProperty("fileLogLevel") ?: return

	val level = ch.qos.logback.classic.Level.toLevel(fileLogLevel)

	ConfigurableRollingFileAppender.level = level
}