package au.com.touchsafe.alcomeasure.util.logging

import au.com.touchsafe.alcomeasure.DebugMarker
import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.LoggingEvent
import ch.qos.logback.core.rolling.RollingFileAppender

/**
 * Configurable RollingFileAppender, can change logging level programmatically
 */
// Need to use LoggingEvent explicitly in order to allow level setting
open class ConfigurableRollingFileAppender : RollingFileAppender<LoggingEvent>() {

    companion object {
        // INFO by default
        var level: Level = Level.INFO
        var debugMarker: DebugMarker? = null
    }

    override fun subAppend(event: LoggingEvent) {
        if (event.level.isGreaterOrEqual(level)) {
            val eventDebugMarker = DebugMarker.parse(event.marker.name)
            // If no debugMarker, or event marker is not a DebugMarker, or debugMarker >= eventDebugMarker
            if (debugMarker == null || eventDebugMarker == null || debugMarker!!.isGreaterOrEqual(eventDebugMarker)) {
                super.subAppend(event)
            }
        }
    }

    /*
        Don't create a setEnabled function to allow logback.xml to use an <enabled> attribute,
        prevents ability to set enabled variable programmatically
    */
}