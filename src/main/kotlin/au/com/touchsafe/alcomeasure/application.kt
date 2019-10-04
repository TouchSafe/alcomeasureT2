package au.com.touchsafe.alcomeasure

internal val LOGGER = org.slf4j.LoggerFactory.getLogger(AlcoMeasure::class.java)
internal val MESSAGES_BUNDLE: java.util.ResourceBundle = java.util.ResourceBundle.getBundle("messages", java.util.Locale.ENGLISH)
internal val SETTINGS_BUNDLE: java.util.ResourceBundle = java.util.ResourceBundle.getBundle("settings")

suspend fun main() {
	LOGGER.info("TouchSafe 2 AlcoMeasure Integration: STARTED")

	while (true) {
		try {
			val id = Input.getId()
			val user = SqlServer.validateId(id)
			if (user == null) when (id) {
				is Pin -> AlcoMeasure.displayMessage(MESSAGES_BUNDLE.getString("INVALID_PIN"))
				is Rfid -> AlcoMeasure.displayMessage(MESSAGES_BUNDLE.getString("INVALID_RFID"))
			} else {
				val result = AlcoMeasure.performTest(user)
				SqlServer.storeResult(user, result)
			}
		} catch (ex: Throwable) {
			LOGGER.error("An unexpected error occurred:", ex)
		}
	}
}
