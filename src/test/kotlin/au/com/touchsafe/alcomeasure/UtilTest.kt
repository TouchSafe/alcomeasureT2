package au.com.touchsafe.alcomeasure

import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import java.util.*
import java.util.logging.Level
import java.util.logging.Logger

class UtilTest {
    companion object {
        private val settings_properties = Properties()
        // This function is used
        @BeforeAll
        @JvmStatic
        fun beforeTests() {
            settings_properties.clear()
            SETTINGS_PROPERTIES.stringPropertyNames().forEach {propertyName ->
                settings_properties.setProperty(propertyName, SETTINGS_PROPERTIES.getProperty(propertyName))
            }
            SETTINGS_PROPERTIES.clear()
        }
        @AfterAll
        @JvmStatic
        fun afterTests() {
            SETTINGS_PROPERTIES.clear()
            settings_properties.stringPropertyNames().forEach {propertyName ->
                SETTINGS_PROPERTIES.setProperty(propertyName, settings_properties.getProperty(propertyName))
            }
        }
    }

    @Test
    fun testSetMailLogLevel() {
        val level = Level.FINEST
        SETTINGS_PROPERTIES.setProperty("emailLogLevel", level.name)

        setMailLogLevel()

        val logger = Logger.getLogger("com.sun.mail")
        assertEquals(level, logger.level)
        assertEquals(1, logger.handlers.size)
        assertEquals(level, logger.handlers[0].level)
    }

    @Test
    fun testSetNullMailLogLevel() {
        assertNull(SETTINGS_PROPERTIES.getProperty("emailLogLevel"))
        val level = Logger.getLogger("com.sun.mail").level
        TestAppender.events.clear()

        setMailLogLevel()

        assertEquals(0, TestAppender.events.size)
        assertEquals(level, Logger.getLogger("com.sun.mail").level)
        TestAppender.events.clear()
    }

    @Test
    fun testSetUnparseableMailLogLevel() {
        SETTINGS_PROPERTIES.setProperty("emailLogLevel", "INVALID LEVEL")
        val level = Logger.getLogger("com.sun.mail").level
        TestAppender.events.clear()

        setMailLogLevel()

        assertEquals(1, TestAppender.events.size)
        assertEquals("Could not parse emailLogLevel \"INVALID LEVEL\"", TestAppender.events[0].message)
        assertEquals(level, Logger.getLogger("com.sun.mail").level)
        TestAppender.events.clear()
    }
}