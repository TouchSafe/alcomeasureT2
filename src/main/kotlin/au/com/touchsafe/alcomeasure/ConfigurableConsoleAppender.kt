package au.com.touchsafe.alcomeasure

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.spi.LoggingEvent
import ch.qos.logback.core.ConsoleAppender

// Toggleable ConsoleAppender, intending on using this for --debug flag
// Need to use LoggingEvent explicitly in order to allow level setting
open class ConfigurableConsoleAppender : ConsoleAppender<LoggingEvent>() {

    companion object {
        // INFO by default
        var level: Level = Level.INFO
    }

    override fun subAppend(event: LoggingEvent) {
        if (event.level.isGreaterOrEqual(level)) {
            super.subAppend(event)
        }
    }

    /*
        Don't create a setEnabled function to allow logback.xml to use an <enabled> attribute,
        prevents ability to set enabled variable programmatically
    */
}