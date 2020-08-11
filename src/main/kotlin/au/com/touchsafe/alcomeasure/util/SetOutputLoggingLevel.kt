package au.com.touchsafe.alcomeasure.util

import au.com.touchsafe.alcomeasure.util.logging.DebugMarker
import au.com.touchsafe.alcomeasure.SETTINGS_PROPERTIES
import au.com.touchsafe.alcomeasure.util.logging.appenders.ConfigurableConsoleAppender
import au.com.touchsafe.alcomeasure.util.logging.appenders.ConfigurableRollingFileAppender
import ch.qos.logback.classic.Level

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
	val levelAndMarker = getLevelAndMarker(consoleLogLevel)

	ConfigurableConsoleAppender.debugMarker = levelAndMarker.second
	ConfigurableConsoleAppender.level = levelAndMarker.first
}

/**
 * Sets logging level for [ConfigurableRollingFileAppender] from property fileLogLevel in [SETTINGS_PROPERTIES]
 */
fun setFileLoggingLevel() {
	val fileLogLevel = SETTINGS_PROPERTIES.getProperty("fileLogLevel") ?: return
	val levelAndMarker = getLevelAndMarker(fileLogLevel)

	ConfigurableRollingFileAppender.debugMarker = levelAndMarker.second
	ConfigurableRollingFileAppender.level = levelAndMarker.first
}

/**
 * Gets the Level and DebugMarker from the supplied logLevelStr
 * @param logLevelStr The log level for a Configurable Appender, set in the settings.properties
 * @return Pair containing the parsed Level and DebugMarker
 */
fun getLevelAndMarker(logLevelStr: String): Pair<Level, DebugMarker?> {
	var level = Level.DEBUG
	val marker = DebugMarker.parse(logLevelStr)
	// If marker != null, Level is DEBUG
	if (marker == null) {
		// Level is not a DebugMarker, set level
		level = Level.toLevel(logLevelStr)
	}
	return Pair(level, marker)
}