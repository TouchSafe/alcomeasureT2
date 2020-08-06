package au.com.touchsafe.alcomeasure.util

import au.com.touchsafe.alcomeasure.SETTINGS_PROPERTIES

fun mailAllReports() = (SETTINGS_PROPERTIES.getProperty("mailAllReports") ?: "false").toBoolean()