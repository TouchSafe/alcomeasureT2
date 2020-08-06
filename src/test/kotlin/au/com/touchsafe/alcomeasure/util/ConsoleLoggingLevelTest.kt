package au.com.touchsafe.alcomeasure.util

import au.com.touchsafe.alcomeasure.ConfigurableConsoleAppender
import au.com.touchsafe.alcomeasure.SETTINGS_PROPERTIES
import ch.qos.logback.classic.Level
import org.junit.jupiter.api.*
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
    fun testSetConsoleLoggingLevelToDebug() {
        val level = Level.DEBUG
        SETTINGS_PROPERTIES.setProperty("consoleLogLevel", level.toString())

        setConsoleLoggingLevel()

        Assertions.assertEquals(level, ConfigurableConsoleAppender.level)
    }

    @Test
    fun testSetConsoleLoggingLevelToNull() {
        //TODO: BeforeEach and AfterEach aren't running
        SETTINGS_PROPERTIES.clear()
        val level = ConfigurableConsoleAppender.level
        Assertions.assertNull(SETTINGS_PROPERTIES.getProperty("consoleLogLevel"))

        setConsoleLoggingLevel()

        Assertions.assertEquals(level, ConfigurableConsoleAppender.level)
    }
}