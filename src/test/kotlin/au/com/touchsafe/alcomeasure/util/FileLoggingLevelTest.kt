package au.com.touchsafe.alcomeasure.util

import au.com.touchsafe.alcomeasure.SETTINGS_PROPERTIES
import au.com.touchsafe.alcomeasure.util.logging.DebugMarker
import au.com.touchsafe.alcomeasure.util.logging.appenders.ConfigurableRollingFileAppender
import ch.qos.logback.classic.Level
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import java.util.*

class FileLoggingLevelTest {
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
    fun testSetFileLoggingLevelToLevels() {
        val levels = listOf<Level>(Level.ALL, Level.DEBUG, Level.ERROR, Level.INFO, Level.OFF, Level.TRACE, Level.WARN)
        levels.forEach {level ->
            SETTINGS_PROPERTIES.setProperty("fileLogLevel", level.toString())
            setFileLoggingLevel()
            assertEquals(level, ConfigurableRollingFileAppender.level)
        }
    }

    @Test
    fun testSetFileLoggingLevelToNull() {
        //TODO: BeforeEach and AfterEach aren't running
        SETTINGS_PROPERTIES.clear()
        val level = ConfigurableRollingFileAppender.level
        assertNull(SETTINGS_PROPERTIES.getProperty("fileLogLevel"))
        setFileLoggingLevel()
        assertEquals(level, ConfigurableRollingFileAppender.level)
    }

    @Test
    fun testSetFileDebugMarker() {
        val markers = listOf(DebugMarker.DEBUG1, DebugMarker.DEBUG2, DebugMarker.DEBUG3, DebugMarker.DEBUG4, DebugMarker.DEBUG5)
        markers.forEach {marker ->
            SETTINGS_PROPERTIES.setProperty("fileLogLevel", marker.name)
            setFileLoggingLevel()
            assertEquals(marker, ConfigurableRollingFileAppender.debugMarker)
        }
    }
}