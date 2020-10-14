package au.com.touchsafe.alcomeasure

import au.com.touchsafe.alcomeasure.util.logging.DebugMarker
import org.jnativehook.keyboard.NativeKeyEvent
import org.jnativehook.keyboard.NativeKeyListener

object InputV2 {

	private const val KEYBOARD_BUFFER_SIZE = 200
	private val KEYBOARD_BUFFER = java.nio.IntBuffer.allocate(KEYBOARD_BUFFER_SIZE)
	internal val KEYBOARD_HOOK = object : NativeKeyListener {
		override fun nativeKeyTyped(event: NativeKeyEvent) {}

		override fun nativeKeyPressed(event: NativeKeyEvent) {}

		override fun nativeKeyReleased(event: NativeKeyEvent) {
			// event.keyChar only works for nativeKeyTyped. event.keyCode works for nativeKeyPressed and nativeKeyReleased
			LOGGER.debug(DebugMarker.DEBUG4.marker, "Key ${event.keyCode} (\"${NativeKeyEvent.getKeyText(event.keyCode)}\") released")
			KEYBOARD_BUFFER.put(event.keyCode)
		}
	}

	fun getId(): Rfid? {
		LOGGER.debug(DebugMarker.DEBUG2.marker, "InputV2.getId called")
		val input = readLine()
//		val id = if (input.contains(';')) {
//			val parts = input.split(';')
//			Rfid(parts[0].toInt(), parts[1].toInt())
//		} else {
//			Pin(input)
//		}
		if (!input.contains(';')) {
			LOGGER.error("Input \"$input\" invalid, does not contain ';'")
			return null
		} else if (input.count { it == ';' } > 1) {
			LOGGER.error("Input \"$input\" invalid, contains more than 1 ';'")
			return null
		}
		val parts = input.split(';')
		val facilityCode = parts[0].toIntOrNull()
		if (facilityCode == null) {
			LOGGER.error("Facility Code \"${parts[0]}\" could not be parsed as an integer")
			return null
		}
		val cardNumber = parts[1].toIntOrNull()
		if (cardNumber == null) {
			LOGGER.error("Card Number \"${parts[1]}\" could not be parsed as an integer")
			return null
		}
		val id = Rfid(facilityCode, cardNumber)
		LOGGER.info("Input gotten: $id")
		return id
	}

	private fun readLine(): String {
		LOGGER.debug(DebugMarker.DEBUG2.marker, "InputV2.readline called")
		KEYBOARD_BUFFER.clear()
		var newLineIndex = indexOfEnterKey()
		while (newLineIndex == -1) {
			Thread.sleep(100)
			newLineIndex = indexOfEnterKey()
		}
		val line = KEYBOARD_BUFFER.array().take(newLineIndex).map { NativeKeyEvent.getKeyText(it) }.joinToString("").replace("Semicolon", ";")
		LOGGER.debug(DebugMarker.DEBUG1.marker, "Read line \"$line\"")
		return line
	}

	private fun indexOfEnterKey(): Int {
		LOGGER.debug(DebugMarker.DEBUG5.marker, "InputV2.indexOfEnterKey called")
		val buffer = KEYBOARD_BUFFER.array().take(KEYBOARD_BUFFER.position())
		// Index of Enter keyCode
		val index = buffer.indexOf(NativeKeyEvent.VC_ENTER)
		LOGGER.debug(DebugMarker.DEBUG5.marker, "Index of ${NativeKeyEvent.VC_ENTER} in \"$buffer\" is $index")
		return index
	}
}

// NOTE: `Pin` is not currently supported, so it has been commented out.
//sealed class Id
//data class Pin(val pin: String) : Id()
data class RfidV2(val facilityCode: Int, val cardNumber: Int) // : Id()
