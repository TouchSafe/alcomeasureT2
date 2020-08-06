package au.com.touchsafe.alcomeasure

import ch.qos.logback.classic.spi.LoggingEvent
import ch.qos.logback.core.AppenderBase
import java.util.ArrayList

// Log appender allowing tests to read logged events
class TestAppender : AppenderBase<LoggingEvent>() {
    override fun append(e: LoggingEvent) {
        events.add(e)
    }

    companion object {
        var events: MutableList<LoggingEvent> = ArrayList<LoggingEvent>()
    }
}