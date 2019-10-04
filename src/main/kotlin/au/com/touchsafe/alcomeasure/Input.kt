package au.com.touchsafe.alcomeasure

object Input {

	fun getId(): Id {
		val input = readLine()!!
		val id = if (input.contains(':')) {
			val parts = input.split(':')
			Rfid(parts[0].toInt(), parts[1].toInt())
		} else {
			Pin(input)
		}
		LOGGER.debug("Input detected: $id")
		return id
	}
}

sealed class Id()
data class Pin(val pin: String) : Id()
data class Rfid(val facilityCode: Int, val cardNumber: Int) : Id()
