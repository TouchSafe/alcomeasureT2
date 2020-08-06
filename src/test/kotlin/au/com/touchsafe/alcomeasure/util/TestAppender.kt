package au.com.touchsafe.alcomeasure.util

import ch.qos.logback.classic.spi.LoggingEvent
import ch.qos.logback.core.AppenderBase

// Log appender allowing tests to read logged events
class TestAppender : AppenderBase<LoggingEvent>() {
    override fun append(event: LoggingEvent) {
        events.add(event)
    }

    companion object {
        val events = mutableListOf<LoggingEvent>()
    }
}