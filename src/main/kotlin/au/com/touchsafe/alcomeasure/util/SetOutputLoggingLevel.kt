package au.com.touchsafe.alcomeasure.util

import au.com.touchsafe.alcomeasure.DebugMarker
import au.com.touchsafe.alcomeasure.util.logging.ConfigurableConsoleAppender
import au.com.touchsafe.alcomeasure.util.logging.ConfigurableRollingFileAppender
import au.com.touchsafe.alcomeasure.SETTINGS_PROPERTIES
import java.lang.IllegalArgumentException

/**
 * Sets logging levels for Configurable Appenders
 * Runs [setConsoleLoggingLevel] and [setFileLoggingLevel]
 */
fun setOutputLoggingLevels() {
	setConsoleLoggingLevel()
	setFileLoggingLevel()
}

/**
 * Sets logging level for [ConfigurableConsoleAppender] from property consoleLogLevel in [SETTINGS_PROPERTIES]
 */
fun setConsoleLoggingLevel() {
	val consoleLogLevel = SETTINGS_PROPERTIES.getProperty("consoleLogLevel") ?: return

	var level = ch.qos.logback.classic.Level.DEBUG
	val marker = DebugMarker.parse(consoleLogLevel)
	// If marker != null, Level is DEBUG
	if (marker == null) {
		// Level is not a DebugMarker, set level
		level = ch.qos.logback.classic.Level.toLevel(consoleLogLevel)
	}

	ConfigurableConsoleAppender.debugMarker = marker
	ConfigurableConsoleAppender.level = level
}

/**
 * Sets logging level for [ConfigurableRollingFileAppender] from property fileLogLevel in [SETTINGS_PROPERTIES]
 */
fun setFileLoggingLevel() {
	val fileLogLevel = SETTINGS_PROPERTIES.getProperty("fileLogLevel") ?: return

	var level = ch.qos.logback.classic.Level.DEBUG
	val marker = DebugMarker.parse(fileLogLevel)
	// If marker != null, Level is DEBUG
	if (marker == null) {
		// Level is not a DebugMarker, set level
		level = ch.qos.logback.classic.Level.toLevel(fileLogLevel)
	}

	ConfigurableConsoleAppender.debugMarker = marker
	ConfigurableRollingFileAppender.level = level
}