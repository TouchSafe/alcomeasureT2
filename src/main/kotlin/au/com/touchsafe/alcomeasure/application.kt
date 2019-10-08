package au.com.touchsafe.alcomeasure

import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle

internal val LOGGER = org.slf4j.LoggerFactory.getLogger(AlcoMeasure::class.java)
internal val MESSAGES_BUNDLE: java.util.ResourceBundle = java.util.ResourceBundle.getBundle("messages", java.util.Locale.ENGLISH)
internal val SETTINGS_BUNDLE: java.util.ResourceBundle = java.util.ResourceBundle.getBundle("settings")

suspend fun main() {
	LOGGER.info("TouchSafe 2 AlcoMeasure Integration: STARTED")

	while (true) {
		try {
			val id = Input.getId()
			val connection = SqlServer.DB_FACTORY.create().awaitSingle()
			try {
				val user = SqlServer.validateId(connection, id)
				when {
					user != null -> AlcoMeasure.performTest(user)?.let {
						SqlServer.storeResult(connection, user, it)
						// TODO: Check if a message needs to be displayed and display if required.
						// TODO: Check if a notification needs to be sent and send if required.
					}
					id is Pin -> AlcoMeasure.displayMessage(MESSAGES_BUNDLE.getString("INVALID_PIN"))
					id is Rfid -> AlcoMeasure.displayMessage(MESSAGES_BUNDLE.getString("INVALID_RFID"))
				}
			} finally {
				connection.close().awaitFirstOrNull()
			}
		} catch (ex: Throwable) {
			LOGGER.error("An unexpected error occurred:", ex)
		}
	}
}
