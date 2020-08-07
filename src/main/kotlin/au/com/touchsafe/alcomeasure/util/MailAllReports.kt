package au.com.touchsafe.alcomeasure.util

import au.com.touchsafe.alcomeasure.SETTINGS_PROPERTIES

/**
 * Gets boolean value of mailAllReports in [SETTINGS_PROPERTIES]
 *
 * If mailAllReports is "true" (case insensitive), returns true.
 * Otherwise it returns false if it is anything else, or is unset
 *
 * @return True if mailAllReports is "true", otherwise returns False
 */
fun mailAllReports() = (SETTINGS_PROPERTIES.getProperty("mailAllReports") ?: "false").toBoolean()