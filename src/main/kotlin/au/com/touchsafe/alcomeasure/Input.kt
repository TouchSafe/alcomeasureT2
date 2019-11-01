package au.com.touchsafe.alcomeasure

object Input {

//	private val reader = java.util.Scanner(System.`in`)

	fun getId(): Id {
		// TODO: Try: while (System.`in`.available() > 0) System.`in`.read()
		val input = readLine()!! // TODO: If you tap your card multiple times, the inputs are buffered, and multiple tests will be initiated!!!!!
//		reader.nextLine()
//		val input = reader.next()!!
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
