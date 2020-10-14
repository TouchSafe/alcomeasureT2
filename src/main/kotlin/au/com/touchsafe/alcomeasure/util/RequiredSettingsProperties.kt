package au.com.touchsafe.alcomeasure.util

import au.com.touchsafe.alcomeasure.LOGGER
import au.com.touchsafe.alcomeasure.SETTINGS_PROPERTIES
import au.com.touchsafe.alcomeasure.util.logging.DebugMarker

/**
 * Checks that each [RequiredSettingsProperty] in the list is present in [SETTINGS_PROPERTIES], and logs an error for each that isn't found
 * @return Whether all of the required properties are present
 */
fun List<RequiredSettingsProperty>.checkPresent(): Boolean {
    var allPresent = true
    this.forEach { requiredSettingsProperty ->
        if (SETTINGS_PROPERTIES.getProperty(requiredSettingsProperty.name) == null) {
            LOGGER.error(requiredSettingsProperty.notFoundMessage)
            allPresent = false
        }
    }
    if (allPresent) {
        LOGGER.debug(DebugMarker.DEBUG2.marker, "All required settings successfully loaded from settings.properties")
    }
    return allPresent
}

/**
 * Data class for a required entry in [SETTINGS_PROPERTIES]
 * @param name The key / variable name in the entry
 * @param notFoundMessage The message to be logged if the entry cannot be found
 */
data class RequiredSettingsProperty(val name: String, val notFoundMessage: String = "Setting $name not found in settings.properties")
