package au.com.touchsafe.alcomeasure.util

import au.com.touchsafe.alcomeasure.SETTINGS_PROPERTIES
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import java.util.*

internal class RequiredSettingsPropertiesTest {
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
    fun testCheckPresentAllPresent() {
        SETTINGS_PROPERTIES.clear()
        val requiredSettingsProperties = listOf(
                RequiredSettingsProperty("test"),
                RequiredSettingsProperty("test1"),
                RequiredSettingsProperty("test2"),
                RequiredSettingsProperty("test3")
        )
        SETTINGS_PROPERTIES.setProperty("test", "abc")
        SETTINGS_PROPERTIES.setProperty("test1", "def")
        SETTINGS_PROPERTIES.setProperty("test2", "ghi")
        SETTINGS_PROPERTIES.setProperty("test3", "jkl")

        assertTrue(requiredSettingsProperties.checkPresent())
    }

    @Test
    fun testCheckPresentNoneRequired() {
        val requiredSettingsProperties = listOf<RequiredSettingsProperty>()
        SETTINGS_PROPERTIES.clear()

        assertTrue(requiredSettingsProperties.checkPresent())
    }

    @Test
    fun testCheckPresentNoneRequiredSomePresent() {
        val requiredSettingsProperties = listOf<RequiredSettingsProperty>()
        SETTINGS_PROPERTIES.clear()
        SETTINGS_PROPERTIES.setProperty("test", "abc")
        SETTINGS_PROPERTIES.setProperty("test1", "def")
        SETTINGS_PROPERTIES.setProperty("test2", "ghi")
        SETTINGS_PROPERTIES.setProperty("test3", "jkl")

        assertTrue(requiredSettingsProperties.checkPresent())
    }

    @Test
    fun testCheckPresentSomeRequiredNonePresent() {
        val requiredSettingsProperties = listOf(
                RequiredSettingsProperty("test"),
                RequiredSettingsProperty("test1"),
                RequiredSettingsProperty("test2"),
                RequiredSettingsProperty("test3")
        )
        SETTINGS_PROPERTIES.clear()

        assertFalse(requiredSettingsProperties.checkPresent())
    }

    @Test
    fun testCheckPresentSomeRequiredMorePresent() {
        val requiredSettingsProperties = listOf(
                RequiredSettingsProperty("test"),
                RequiredSettingsProperty("test1")
        )
        SETTINGS_PROPERTIES.clear()
        SETTINGS_PROPERTIES.setProperty("test", "abc")
        SETTINGS_PROPERTIES.setProperty("test1", "def")
        SETTINGS_PROPERTIES.setProperty("test2", "ghi")
        SETTINGS_PROPERTIES.setProperty("test3", "jkl")

        assertTrue(requiredSettingsProperties.checkPresent())
    }

    @Test
    fun testCheckPresentSomeRequiredLessPresent() {
        val requiredSettingsProperties = listOf(
                RequiredSettingsProperty("test"),
                RequiredSettingsProperty("test1"),
                RequiredSettingsProperty("test2"),
                RequiredSettingsProperty("test3")
        )
        SETTINGS_PROPERTIES.clear()
        SETTINGS_PROPERTIES.setProperty("test", "abc")
        SETTINGS_PROPERTIES.setProperty("test1", "def")

        assertFalse(requiredSettingsProperties.checkPresent())
    }

    @Test
    fun testCheckPresentSomeRequiredDifferentPresent() {
        val requiredSettingsProperties = listOf(
                RequiredSettingsProperty("test"),
                RequiredSettingsProperty("test1")
        )
        SETTINGS_PROPERTIES.clear()
        SETTINGS_PROPERTIES.setProperty("test2", "ghi")
        SETTINGS_PROPERTIES.setProperty("test3", "jkl")

        assertFalse(requiredSettingsProperties.checkPresent())
    }

    @Test
    fun testCheckPresentSomeRequiredSomeDifferentPresent() {
        val requiredSettingsProperties = listOf(
                RequiredSettingsProperty("test"),
                RequiredSettingsProperty("test1")
        )
        SETTINGS_PROPERTIES.clear()
        SETTINGS_PROPERTIES.setProperty("test", "abc")
        SETTINGS_PROPERTIES.setProperty("test2", "ghi")
        SETTINGS_PROPERTIES.setProperty("test3", "jkl")

        assertFalse(requiredSettingsProperties.checkPresent())
    }
}