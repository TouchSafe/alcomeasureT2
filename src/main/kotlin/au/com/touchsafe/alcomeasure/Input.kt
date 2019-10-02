package au.com.touchsafe.alcomeasure

object Input {

	val applicationProperties = java.util.ResourceBundle.getBundle("application")

	suspend fun getId(): Id {
		val id = Pin(readLine()!!) // TODO: Replace.
		LOGGER.debug("Input detected: $id")
		return id
	}
}

sealed class Id()

// RFID Reader - CCID
data class Rfid(val facilityCode: String, val cardNumber: String) : Id()

// PIN - Keypad
data class Pin(val pin: String) : Id()
