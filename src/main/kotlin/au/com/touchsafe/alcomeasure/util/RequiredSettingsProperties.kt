package au.com.touchsafe.alcomeasure.util

import au.com.touchsafe.alcomeasure.LOGGER
import au.com.touchsafe.alcomeasure.SETTINGS_PROPERTIES
import au.com.touchsafe.alcomeasure.util.logging.DebugMarker

/**
 * Checks that the [RequiredSettingsProperty] is present in [SETTINGS_PROPERTIES], and logs an error if it isn't found
 * @return Whether the required property is present
 */
fun RequiredSettingsProperty.isPresent(): Boolean {
    if (SETTINGS_PROPERTIES.getProperty(this.name) == null) {
        LOGGER.error(this.notFoundMessage)
        return false
    }
    return true
}

/**
 * Checks that each [RequiredSettingsProperty] in the list is present in [SETTINGS_PROPERTIES], and logs an error for each that isn't found
 * @return Whether all of the required properties are present
 */
fun List<RequiredSettingsProperty>.checkAllPresent(): Boolean {
    var allPresent = true
    this.forEach { requiredSettingsProperty ->
        if (!requiredSettingsProperty.isPresent()) {
            allPresent = false
        }
    }
    if (allPresent) {
        LOGGER.debug(DebugMarker.DEBUG2.marker, "All required settings successfully loaded from settings.properties")
    }
    return allPresent
}

/**
 * Checks that the [RequiredSettingsProperty]'s value in [SETTINGS_PROPERTIES] matches it's
 * [RequiredSettingsProperty.regex], and logs an error if it doesn't match
 * @return Whether the property matches it's regex
 */
fun RequiredSettingsProperty.matchesRegex(): Boolean {
    if (!this.isPresent()) {
        return false
    }
    if (!this.regex.matches(SETTINGS_PROPERTIES.getProperty(this.name))) {
        LOGGER.error(this.doesNotMatchMessage)
        return false
    }
    return true
}

/**
 * Checks that each [RequiredSettingsProperty] in the list's value in [SETTINGS_PROPERTIES] matches it's
 * [RequiredSettingsProperty.regex], and logs an error for each that doesn't match
 * @return Whether all of the required properties match their regex
 */
fun List<RequiredSettingsProperty>.checkAllMatchRegex(): Boolean {
    var allMatch = true
    this.forEach { requiredSettingsProperty ->
        if (!requiredSettingsProperty.matchesRegex()) {
            allMatch = false
        }
    }
    if (allMatch) {
        LOGGER.debug(DebugMarker.DEBUG2.marker, "All required settings match their respective regex values")
    }
    return allMatch
}

/**
 * Data class for a required entry in [SETTINGS_PROPERTIES]
 * @param name The key / variable name in the entry
 * @param notFoundMessage The message to be logged if the entry cannot be found
 */
data class RequiredSettingsProperty(val name: String, val notFoundMessage: String = "Setting $name not found in settings.properties", val regex: Regex = Regex(".*"), val doesNotMatchMessage: String = "Setting $name does not match the required regex \"${regex.pattern}\"")
