package au.com.touchsafe.alcomeasure.util.logging

import au.com.touchsafe.alcomeasure.LOGGER
import ch.qos.logback.classic.Level
import org.junit.jupiter.api.Test

class ConfigurableConsoleAppenderTest {

    @Test
    // Haven't been able to figure out a way to do assertions on this yet
    /*
        Best option I can think of would be to create an appender, like TestAppender, but extending
        ToggleableConsoleAppender, and add it to logback.xml.
     */
    fun testDebugLevel() {
        ConfigurableConsoleAppender.level = Level.INFO
        LOGGER.debug("This message should not appear")
        ConfigurableConsoleAppender.level = Level.DEBUG
        LOGGER.debug("This message should appear")
    }
}