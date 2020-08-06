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

fun setConsoleDebugLogging() {
	val isDebugStr = SETTINGS_PROPERTIES.getProperty("consoleDebug")

	if (isDebugStr == null) {
		ToggleableConsoleAppender.enabled = false
	} else {
	 	ToggleableConsoleAppender.enabled = isDebugStr.toBoolean()
	}
}