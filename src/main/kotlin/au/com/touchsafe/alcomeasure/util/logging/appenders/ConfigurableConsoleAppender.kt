package au.com.touchsafe.alcomeasure.util.logging.appenders

import au.com.touchsafe.alcomeasure.util.logging.DebugMarker
import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.LoggingEvent
import ch.qos.logback.core.ConsoleAppender

/**
 * Configurable ConsoleAppender, can change logging level programmatically
 */
// Need to use LoggingEvent explicitly in order to allow level setting
open class ConfigurableConsoleAppender : ConsoleAppender<LoggingEvent>() {

    companion object {
        // INFO by default
        var level: Level = Level.INFO
        var debugMarker: DebugMarker? = null
    }

    override fun subAppend(event: LoggingEvent) {
        if (event.level.isGreaterOrEqual(level)) {
            val eventDebugMarker = if (event.marker != null) {
                DebugMarker.parse(event.marker.name)
            } else {
                null
            }
            // If no debugMarker, or event marker is not a DebugMarker, or debugMarker <= eventDebugMarker
            if (debugMarker == null || eventDebugMarker == null || debugMarker!!.isLesserOrEqual(eventDebugMarker)) {
                super.subAppend(event)
            }
        }
    }

    /*
        Don't create a setEnabled function to allow logback.xml to use an <enabled> attribute,
        prevents ability to set enabled variable programmatically
    */
}