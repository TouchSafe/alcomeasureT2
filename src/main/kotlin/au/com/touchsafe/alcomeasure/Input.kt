package au.com.touchsafe.alcomeasure

import au.com.touchsafe.alcomeasure.util.logging.DebugMarker

object Input {

	private const val KEYBOARD_BUFFER_SIZE = 200
	private val KEYBOARD_BUFFER = java.nio.CharBuffer.allocate(KEYBOARD_BUFFER_SIZE)
	internal val KEYBOARD_HOOK = lc.kra.system.keyboard.GlobalKeyboardHook(true).apply {
		addKeyListener(object : lc.kra.system.keyboard.event.GlobalKeyAdapter() {

			override fun keyPressed(event: lc.kra.system.keyboard.event.GlobalKeyEvent) {}

			override fun keyReleased(event: lc.kra.system.keyboard.event.GlobalKeyEvent) {
				LOGGER.debug(DebugMarker.DEBUG4.marker, "Key \"${event.keyChar}\" pressed on device with handle \"${event.deviceHandle}\"")
				KEYBOARD_BUFFER.put(event.keyChar)
			}
		})
	}

	fun getId(): Rfid {
		LOGGER.debug(DebugMarker.DEBUG2.marker, "Input.getId called")
		val input = readLine()
//		val id = if (input.contains(';')) {
//			val parts = input.split(';')
//			Rfid(parts[0].toInt(), parts[1].toInt())
//		} else {
//			Pin(input)
//		}
		val parts = input.split(';')
		val id = Rfid(parts[0].toInt(), parts[1].toInt())
		LOGGER.info("Input gotten: $id")
		return id
	}

	private fun readLine(): String {
		LOGGER.debug(DebugMarker.DEBUG2.marker, "Input.readline called")
		KEYBOARD_BUFFER.clear()
		var newLineIndex = indexOfCarriageReturn()
		while (newLineIndex == -1) {
			Thread.sleep(100)
			newLineIndex = indexOfCarriageReturn()
		}
		val line = KEYBOARD_BUFFER.array().take(newLineIndex).filter { it != '\u0000' }.joinToString("")
		LOGGER.debug(DebugMarker.DEBUG1.marker, "Read line \"$line\"")
		return line
	}

	private fun indexOfCarriageReturn(): Int {
		LOGGER.debug(DebugMarker.DEBUG5.marker, "Input.indexOfCarriageReturn called")
		val buffer = KEYBOARD_BUFFER.array().take(KEYBOARD_BUFFER.position())
		val index = buffer.indexOf('\r')
		LOGGER.debug(DebugMarker.DEBUG5.marker, "Index of '\\r' in \"$buffer\" is $index")
		return index
	}
}

// NOTE: `Pin` is not currently supported, so it has been commented out.
//sealed class Id
//data class Pin(val pin: String) : Id()
data class Rfid(val facilityCode: Int, val cardNumber: Int) // : Id()
