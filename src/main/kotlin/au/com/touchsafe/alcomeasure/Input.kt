package au.com.touchsafe.alcomeasure

object Input {

	private const val KEYBOARD_BUFFER_SIZE = 200
	private val KEYBOARD_BUFFER = java.nio.CharBuffer.allocate(KEYBOARD_BUFFER_SIZE)
	internal val KEYBOARD_HOOK = lc.kra.system.keyboard.GlobalKeyboardHook(true).apply {
		addKeyListener(object : lc.kra.system.keyboard.event.GlobalKeyAdapter() {

			override fun keyPressed(event: lc.kra.system.keyboard.event.GlobalKeyEvent) {}

			override fun keyReleased(event: lc.kra.system.keyboard.event.GlobalKeyEvent) {
				KEYBOARD_BUFFER.put(event.keyChar)
			}
		})
	}

	fun getId(): Rfid {
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
		KEYBOARD_BUFFER.clear()
		var newLineIndex = indexOfCarriageReturn()
		while (newLineIndex == -1) {
			Thread.sleep(100)
			newLineIndex = indexOfCarriageReturn()
		}
		return KEYBOARD_BUFFER.array().take(newLineIndex).joinToString("")
	}

	private fun indexOfCarriageReturn(): Int {
		val buffer = KEYBOARD_BUFFER.array().take(KEYBOARD_BUFFER.position())
		return buffer.indexOf('\r')
	}
}

// NOTE: `Pin` is not currently supported, so it has been commented out.
//sealed class Id
//data class Pin(val pin: String) : Id()
data class Rfid(val facilityCode: Int, val cardNumber: Int) // : Id()
