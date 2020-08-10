package au.com.touchsafe.alcomeasure.util.logging

import org.slf4j.Marker
import org.slf4j.MarkerFactory

/**
 * Markers used to denote different levels of debug log (larger number = more verbose)
 */
class DebugMarker(val level: Int, val name: String) {
	val marker: Marker = MarkerFactory.getMarker(name)

	/**
	 * Whether the DebugMarker is lesser than, or equal to the argument other
	 * For example: [DEBUG1], [DEBUG2] and [DEBUG3] are lesser than or equal to [DEBUG3]
	 * @param other The argument that the DebugLevel is compared against
	 * @return True if the DebugMarker's level is less than or equal to the argument [other]'s level, otherwise false
	 */
	fun isLesserOrEqual(other: DebugMarker): Boolean {
		return level <= other.level
	}

	companion object {
		val DEBUG1 = DebugMarker(1, "DEBUG1")  // Light logging, slightly more verbose than normal debug logging
		val DEBUG2 = DebugMarker(2, "DEBUG2")  // Moderate logging, contains function entrances & exits
		val DEBUG3 = DebugMarker(3, "DEBUG3")  // Bulky logging
		val DEBUG4 = DebugMarker(4, "DEBUG4")  // Heavy logging
		val DEBUG5 = DebugMarker(5, "DEBUG5")  // Very heavy logging, not recommended

		fun parse(name: String): DebugMarker? {
			when (name) {
				"DEBUG1" -> {
					return DEBUG1
				}
				"DEBUG2" -> {
					return DEBUG2
				}
				"DEBUG3" -> {
					return DEBUG3
				}
				"DEBUG4" -> {
					return DEBUG4
				}
				"DEBUG5" -> {
					return DEBUG5
				}
			}
			return null
		}
	}
}