package au.com.touchsafe.alcomeasure

import ch.qos.logback.classic.spi.LoggingEvent
import ch.qos.logback.core.ConsoleAppender

// Toggleable ConsoleAppender, intending on using this for --debug flag
// Need to use LoggingEvent explicitly in order to allow level setting
open class ToggleableConsoleAppender : ConsoleAppender<LoggingEvent>() {

    companion object Attributes {
        // Enabled by default
        var enabled: Boolean = true
    }

    override fun subAppend(event: LoggingEvent) {
        if (enabled) {
            super.subAppend(event)
        }
    }

    // Used by logback.xml when <enabled> attribute is used
    @Suppress("unused")
    fun setEnabled(enabled: Boolean) {
        Attributes.enabled = enabled
    }
}