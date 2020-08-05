package au.com.touchsafe.alcomeasure

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.logging.Level
import java.util.logging.Logger

class UtilTest {

    @Test
    fun testSetMailLogLevel() {
        val level = Level.FINEST

        SETTINGS_PROPERTIES.clear()
        SETTINGS_PROPERTIES.setProperty("emailLogLevel", level.name)
        setMailLogLevel()

        val logger = Logger.getLogger("com.sun.mail")

        assertEquals(level, logger.level)
        assertEquals(1, logger.handlers.size)
        assertEquals(level, logger.handlers[0].level)
    }
}