package au.com.touchsafe.alcomeasure

import au.com.touchsafe.alcomeasure.util.logging.DebugMarker
import org.jnativehook.keyboard.NativeKeyEvent
import org.jnativehook.keyboard.NativeKeyListener

object InputV2 {

	private const val KEYBOARD_BUFFER_SIZE = 200
	private val KEYBOARD_BUFFER = java.nio.CharBuffer.allocate(KEYBOARD_BUFFER_SIZE)
	internal val KEYBOARD_HOOK = object : NativeKeyListener {
		override fun nativeKeyTyped(event: NativeKeyEvent) {
			// event.keyChar only works for nativeKeyTyped. event.keyCode works for nativeKeyPressed and nativeKeyReleased15;3420x
			LOGGER.debug(DebugMarker.DEBUG4.marker, "Key \"${event.keyChar}\" typed")
			KEYBOARD_BUFFER.put(event.keyChar)
		}

		override fun nativeKeyPressed(event: NativeKeyEvent) {}

		override fun nativeKeyReleased(event: NativeKeyEvent) {}
	}

	fun getId(): Rfid {
		LOGGER.debug(DebugMarker.DEBUG2.marker, "InputV2.getId called")
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
		LOGGER.debug(DebugMarker.DEBUG2.marker, "InputV2.readline called")
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
		LOGGER.debug(DebugMarker.DEBUG5.marker, "InputV2.indexOfCarriageReturn called")
		val buffer = KEYBOARD_BUFFER.array().take(KEYBOARD_BUFFER.position())
		val index = buffer.indexOf('\r')
		LOGGER.debug(DebugMarker.DEBUG5.marker, "Index of '\\r' in \"$buffer\" is $index")
		return index
	}
}

// NOTE: `Pin` is not currently supported, so it has been commented out.
//sealed class Id
//data class Pin(val pin: String) : Id()
data class RfidV2(val facilityCode: Int, val cardNumber: Int) // : Id()
