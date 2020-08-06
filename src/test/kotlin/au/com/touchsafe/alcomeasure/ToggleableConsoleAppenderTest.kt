package au.com.touchsafe.alcomeasure

import org.junit.jupiter.api.Test

class ToggleableConsoleAppenderTest {

    @Test
    // Haven't been able to figure out a way to do assertions on this yet
    /*
        Best option I can think of would be to create an appender, like TestAppender, but extending
        ToggleableConsoleAppender, and add it to logback.xml.
     */
    fun testEnabling() {
        ToggleableConsoleAppender.enabled = false
        LOGGER.info("This message should not appear")
        ToggleableConsoleAppender.enabled = true
        LOGGER.info("This message should appear")
    }
}