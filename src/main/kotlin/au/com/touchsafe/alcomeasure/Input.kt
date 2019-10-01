package au.com.touchsafe.alcomeasure

object Input {

	val applicationPropeties = java.util.ResourceBundle.getBundle("application")

	suspend fun getId(): Id {
		return Pin(readLine()!!) // TODO: Replace.
	}
}

sealed class Id()

// RFID Reader - CCID
data class Rfid(val facilityCode: String, val cardNumber: String) : Id()

// PIN - Keypad
data class Pin(val pin: String) : Id()
