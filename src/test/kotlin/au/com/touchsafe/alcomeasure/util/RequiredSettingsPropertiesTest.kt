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

        assertTrue(requiredSettingsProperties.checkAllPresent())
    }

    @Test
    fun testCheckPresentNoneRequired() {
        val requiredSettingsProperties = listOf<RequiredSettingsProperty>()
        SETTINGS_PROPERTIES.clear()

        assertTrue(requiredSettingsProperties.checkAllPresent())
    }

    @Test
    fun testCheckPresentNoneRequiredSomePresent() {
        val requiredSettingsProperties = listOf<RequiredSettingsProperty>()
        SETTINGS_PROPERTIES.clear()
        SETTINGS_PROPERTIES.setProperty("test", "abc")
        SETTINGS_PROPERTIES.setProperty("test1", "def")
        SETTINGS_PROPERTIES.setProperty("test2", "ghi")
        SETTINGS_PROPERTIES.setProperty("test3", "jkl")

        assertTrue(requiredSettingsProperties.checkAllPresent())
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

        assertFalse(requiredSettingsProperties.checkAllPresent())
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

        assertTrue(requiredSettingsProperties.checkAllPresent())
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

        assertFalse(requiredSettingsProperties.checkAllPresent())
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

        assertFalse(requiredSettingsProperties.checkAllPresent())
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

        assertFalse(requiredSettingsProperties.checkAllPresent())
    }

    @Test
    fun testCheckMatchRegexNoRegex() {
        SETTINGS_PROPERTIES.clear()
        SETTINGS_PROPERTIES.setProperty("test", "abc")
        SETTINGS_PROPERTIES.setProperty("test1", "def")
        SETTINGS_PROPERTIES.setProperty("test2", "ghi")
        SETTINGS_PROPERTIES.setProperty("test3", "jkl")

        val requiredSettingsProperties = listOf(
                RequiredSettingsProperty("test"),
                RequiredSettingsProperty("test1"),
                RequiredSettingsProperty("test2"),
                RequiredSettingsProperty("test3"),
        )

        assertTrue(requiredSettingsProperties.checkAllMatchRegex())
    }

    @Test
    fun testCheckMatchRegexAllMatch() {
        SETTINGS_PROPERTIES.clear()
        SETTINGS_PROPERTIES.setProperty("test", "abc")
        SETTINGS_PROPERTIES.setProperty("test1", "def")
        SETTINGS_PROPERTIES.setProperty("test2", "ghi")
        SETTINGS_PROPERTIES.setProperty("test3", "jkl")

        val requiredSettingsProperties = listOf(
                RequiredSettingsProperty("test", regex = Regex("^abc$")),
                RequiredSettingsProperty("test1", regex = Regex("^d.f$")),
                RequiredSettingsProperty("test2", regex = Regex("^.+i$")),
                RequiredSettingsProperty("test3", regex = Regex("^jkl.*$")),
        )

        assertTrue(requiredSettingsProperties.checkAllMatchRegex())
    }
    @Test
    fun testCheckMatchRegexSomeMatch() {
        SETTINGS_PROPERTIES.clear()
        SETTINGS_PROPERTIES.setProperty("test", "abc")
        SETTINGS_PROPERTIES.setProperty("test1", "def")
        SETTINGS_PROPERTIES.setProperty("test2", "ghi")
        SETTINGS_PROPERTIES.setProperty("test3", "jkl")

        val requiredSettingsProperties = listOf(
                RequiredSettingsProperty("test", regex = Regex("^abc$")),
                RequiredSettingsProperty("test1", regex = Regex("^def$")),
                RequiredSettingsProperty("test2", regex = Regex("^abc$")),
                RequiredSettingsProperty("test3", regex = Regex("^abc$")),
        )

        assertFalse(requiredSettingsProperties.checkAllMatchRegex())
    }
    @Test
    fun testCheckMatchRegexNoneMatch() {
        SETTINGS_PROPERTIES.clear()
        SETTINGS_PROPERTIES.setProperty("test", "abc")
        SETTINGS_PROPERTIES.setProperty("test1", "def")
        SETTINGS_PROPERTIES.setProperty("test2", "ghi")
        SETTINGS_PROPERTIES.setProperty("test3", "jkl")

        val requiredSettingsProperties = listOf(
                RequiredSettingsProperty("test", regex = Regex("^z$")),
                RequiredSettingsProperty("test1", regex = Regex("^zz$")),
                RequiredSettingsProperty("test2", regex = Regex("^zzz$")),
                RequiredSettingsProperty("test3", regex = Regex("^51q2z$")),
        )

        assertFalse(requiredSettingsProperties.checkAllMatchRegex())
    }
    @Test
    fun testCheckMatchRegexEmptyRegex() {
        SETTINGS_PROPERTIES.clear()
        SETTINGS_PROPERTIES.setProperty("test", "abc")
        SETTINGS_PROPERTIES.setProperty("test1", "def")
        SETTINGS_PROPERTIES.setProperty("test2", "ghi")
        SETTINGS_PROPERTIES.setProperty("test3", "jkl")

        val requiredSettingsProperties = listOf(
                RequiredSettingsProperty("test", regex = Regex("")),
                RequiredSettingsProperty("test1", regex = Regex("")),
                RequiredSettingsProperty("test2", regex = Regex("")),
                RequiredSettingsProperty("test3", regex = Regex("")),
        )

        assertFalse(requiredSettingsProperties.checkAllMatchRegex())
    }
    @Test
    fun testCheckMatchRegexUndefinedProperty() {
        SETTINGS_PROPERTIES.clear()
        SETTINGS_PROPERTIES.setProperty("test", "abc")
        SETTINGS_PROPERTIES.setProperty("test1", "def")
        SETTINGS_PROPERTIES.setProperty("test2", "ghi")

        val requiredSettingsProperties = listOf(
                RequiredSettingsProperty("test", regex = Regex("")),
                RequiredSettingsProperty("test1", regex = Regex("")),
                RequiredSettingsProperty("test2", regex = Regex("")),
                RequiredSettingsProperty("test3", regex = Regex("")),
        )

        assertFalse(requiredSettingsProperties.checkAllMatchRegex())
    }
}