package au.com.touchsafe.alcomeasure

import java.util.logging.ConsoleHandler
import java.util.logging.Level
import java.util.logging.Logger
import java.util.logging.SimpleFormatter

// Sets logging level for com.sun.mail from property emailLogLevel in SETTINGS_PROPERTIES
fun setMailLogLevel() {
	val logLevelStr = SETTINGS_PROPERTIES.getProperty("emailLogLevel") ?: return
	val level: Level
	try {
		level = Level.parse(logLevelStr)
	} catch (ex: Throwable) {
		LOGGER.warn("Could not parse emailLogLevel \"$logLevelStr\"")
		return
	}

	val logger = Logger.getLogger("com.sun.mail")
	logger.level = level
	val handler = ConsoleHandler()
	handler.formatter = SimpleFormatter()
	handler.level = level
	logger.addHandler(handler)
}

fun setConsoleLoggingLevel() {
	val consoleLogLevel = SETTINGS_PROPERTIES.getProperty("consoleLogLevel") ?: return

	val level = ch.qos.logback.classic.Level.toLevel(consoleLogLevel)

	LevellableConsoleAppender.level = level
}