package au.com.touchsafe.alcomeasure

internal val LOGGER = org.slf4j.LoggerFactory.getLogger(AlcoMeasure::class.java)
internal val MESSAGES_BUNDLE: java.util.ResourceBundle = java.util.ResourceBundle.getBundle("messages", java.util.Locale.ENGLISH)
internal val SETTINGS_PROPERTIES: java.util.Properties = java.util.Properties().apply {
	java.io.FileInputStream("./settings.properties").use { load(it) }
}

fun main() {
	LOGGER.info("TouchSafe 2 AlcoMeasure Integration: STARTED")
	LOGGER.info("Connected keyboards:" + lc.kra.system.keyboard.GlobalKeyboardHook.listKeyboards().map { (key, value) -> " [$key:$value]" }.joinToString(""))

	try {
		while (true) {
			try {
				val id = Input.getId()
				java.sql.DriverManager.getConnection(SqlServer.DB_CONNECTION_URI).use { connection ->
					val user = SqlServer.validateId(connection, id)
					if (user == null) {
//						when (id) {
//							is Pin -> AlcoMeasure.displayMessage(MESSAGES_BUNDLE.getString("INVALID_PIN"))
//							is Rfid -> AlcoMeasure.displayMessage(MESSAGES_BUNDLE.getString("INVALID_RFID"))
//						}
						AlcoMeasure.displayMessage(MESSAGES_BUNDLE.getString("INVALID_RFID"))
					} else {
						AlcoMeasure.performTest(user)?.let { result ->
							SqlServer.storeResult(connection, user, result)
							val emailBody = java.text.MessageFormat.format(MESSAGES_BUNDLE.getString("EMAIL_BODY"), user.firstName, user.surname, "%.8f".format(result.value))
							if (result.value != 0.0)Email.send(Email.TO, MESSAGES_BUNDLE.getString("EMAIL_SUBJECT"), emailBody, "photo1" to result.photo1Uri, "photo2" to result.photo2Uri, "photo3" to result.photo3Uri)
						}
					}
				}
			} catch (ex: Throwable) {
				LOGGER.error("An unexpected error occurred:", ex)
			}
		}
	} finally {
		Input.KEYBOARD_HOOK.shutdownHook()
	}
}
