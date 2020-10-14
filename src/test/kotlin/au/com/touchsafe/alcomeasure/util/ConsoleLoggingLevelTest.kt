package au.com.touchsafe.alcomeasure.util

import au.com.touchsafe.alcomeasure.SETTINGS_PROPERTIES
import au.com.touchsafe.alcomeasure.util.logging.DebugMarker
import au.com.touchsafe.alcomeasure.util.logging.appenders.ConfigurableConsoleAppender
import ch.qos.logback.classic.Level
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import java.util.*

class ConsoleLoggingLevelTest {
    companion object {
        private val settings_properties = Properties()

        @BeforeAll
        @JvmStatic
        fun beforeTests() {
            settings_properties.clear()
            SETTINGS_PROPERTIES.stringPropertyNames().forEach { propertyName ->
                settings_properties.setProperty(propertyName, SETTINGS_PROPERTIES.getProperty(propertyName))
            }
            SETTINGS_PROPERTIES.clear()
        }

        @BeforeEach
        fun beforeEachTest() {
            SETTINGS_PROPERTIES.clear()
        }

        @AfterEach
        fun afterEachTest() {
            SETTINGS_PROPERTIES.clear()
        }

        @AfterAll
        @JvmStatic
        fun afterTests() {
            SETTINGS_PROPERTIES.clear()
            settings_properties.stringPropertyNames().forEach { propertyName ->
                SETTINGS_PROPERTIES.setProperty(propertyName, settings_properties.getProperty(propertyName))
            }
        }
    }

    @Test
    fun testSetConsoleLoggingLevelToLevels() {
        val levels = listOf<Level>(Level.ALL, Level.DEBUG, Level.ERROR, Level.INFO, Level.OFF, Level.TRACE, Level.WARN)
        levels.forEach {level ->
            SETTINGS_PROPERTIES.setProperty("consoleLogLevel", level.toString())
            setConsoleLoggingLevel()
            assertEquals(level, ConfigurableConsoleAppender.level)
        }
    }

    @Test
    fun testSetConsoleLoggingLevelToNull() {
        //TODO: BeforeEach and AfterEach aren't running
        SETTINGS_PROPERTIES.clear()
        val level = ConfigurableConsoleAppender.level
        assertNull(SETTINGS_PROPERTIES.getProperty("consoleLogLevel"))
        setConsoleLoggingLevel()
        assertEquals(level, ConfigurableConsoleAppender.level)
    }

    @Test
    fun testSetConsoleDebugMarker() {
        val markers = listOf(DebugMarker.DEBUG1, DebugMarker.DEBUG2, DebugMarker.DEBUG3, DebugMarker.DEBUG4, DebugMarker.DEBUG5)
        markers.forEach {marker ->
            SETTINGS_PROPERTIES.setProperty("consoleLogLevel", marker.name)
            setConsoleLoggingLevel()
            assertEquals(marker, ConfigurableConsoleAppender.debugMarker)
        }
    }
}