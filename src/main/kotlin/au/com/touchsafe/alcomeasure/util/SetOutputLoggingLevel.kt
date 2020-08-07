package au.com.touchsafe.alcomeasure.util

import au.com.touchsafe.alcomeasure.util.logging.ConfigurableConsoleAppender
import au.com.touchsafe.alcomeasure.util.logging.ConfigurableRollingFileAppender
import au.com.touchsafe.alcomeasure.SETTINGS_PROPERTIES

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

	val level = ch.qos.logback.classic.Level.toLevel(consoleLogLevel)

	ConfigurableConsoleAppender.level = level
}

/**
 * Sets logging level for [ConfigurableRollingFileAppender] from property fileLogLevel in [SETTINGS_PROPERTIES]
 */
fun setFileLoggingLevel() {
	val fileLogLevel = SETTINGS_PROPERTIES.getProperty("fileLogLevel") ?: return

	val level = ch.qos.logback.classic.Level.toLevel(fileLogLevel)

	ConfigurableRollingFileAppender.level = level
}