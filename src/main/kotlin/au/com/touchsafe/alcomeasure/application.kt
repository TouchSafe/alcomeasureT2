package au.com.touchsafe.alcomeasure

internal val LOGGER = org.slf4j.LoggerFactory.getLogger(AlcoMeasure::class.java)
internal val MESSAGES_BUNDLE: java.util.ResourceBundle = java.util.ResourceBundle.getBundle("messages", java.util.Locale.ENGLISH)
internal val SETTINGS_BUNDLE: java.util.ResourceBundle = java.util.ResourceBundle.getBundle("settings")

suspend fun main() {
	LOGGER.info("TouchSafe 2 AlcoMeasure Integration: STARTED")

	while (true) {
		try {
			val id = Input.getId()
			val connection = java.sql.DriverManager.getConnection(SqlServer.DB_CONNECTION_URI)
			@Suppress("ConvertTryFinallyToUseCall")
			try {
				val user = SqlServer.validateId(connection, id)
				when {
					user != null -> AlcoMeasure.performTest(user)?.let { result ->
						SqlServer.storeResult(connection, user, result)
						if (result.value != 0.0) Email.send(Email.TO, "Non-zero breathalyser result", "Name: ${user.firstName} ${user.surname}\nResult: ${result.value}", "photo1" to result.photo1Uri, "photo2" to result.photo2Uri, "photo3" to result.photo3Uri)
					}
					id is Pin -> AlcoMeasure.displayMessage(MESSAGES_BUNDLE.getString("INVALID_PIN"))
					id is Rfid -> AlcoMeasure.displayMessage(MESSAGES_BUNDLE.getString("INVALID_RFID"))
					else -> throw UnsupportedOperationException("This cannot happen.")
				}
			} finally {
				connection.close()
			}
		} catch (ex: Throwable) {
			LOGGER.error("An unexpected error occurred:", ex)
		}
	}
}
