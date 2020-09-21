package au.com.touchsafe.alcomeasure

import au.com.touchsafe.alcomeasure.util.logging.DebugMarker
import au.com.touchsafe.alcomeasure.util.mailAllReports
import au.com.touchsafe.alcomeasure.util.setMailLogLevel
import au.com.touchsafe.alcomeasure.util.setOutputLoggingLevels
import org.apache.commons.lang3.SystemUtils

internal val LOGGER = org.slf4j.LoggerFactory.getLogger(AlcoMeasure::class.java)
internal val MESSAGES_BUNDLE: java.util.ResourceBundle = java.util.ResourceBundle.getBundle("messages", java.util.Locale.ENGLISH)

/**
 * Properties imported from the file ./settings.properties
 */
internal val SETTINGS_PROPERTIES: java.util.Properties = java.util.Properties().apply { java.io.FileInputStream("./settings.properties").use { load(it) } }

fun getOperatingSystemSystemUtils(): String? {
	// System.out.println("Using SystemUtils: " + SystemUtils.OS_NAME);
	return SystemUtils.OS_NAME
}

/**
 * Main function of the application
 *
 * TODO Modifications are needed to get this to show some output on start. At the moment it's not showing anything and it's hard to tell if the program has even started up properly
 */
fun main() {
	setMailLogLevel()
	setOutputLoggingLevels()
	println("TouchSafe 2 AlcoMeasure Integration: STARTED")
	LOGGER.info("TouchSafe 2 AlcoMeasure Integration: STARTED")   // TODO output the version number here

	// TODO: lc.kra.system.* is Windows specific - https://github.com/kristian/system-hook/issues/20
	var os = getOperatingSystemSystemUtils()
	LOGGER.info("OS: $os")
	if (os != "Linux") {
		LOGGER.info("Connected keyboards:" + lc.kra.system.keyboard.GlobalKeyboardHook.listKeyboards().map { (key, value) -> " [$key:$value]" }.joinToString(""))
	}
	// TODO Need to handle connection error here with Redis
	Redis.applicationStarted()

	try {
		LOGGER.debug(DebugMarker.DEBUG1.marker, "Starting main loop")
		while (true) {
			try {
				val id = Input.getId()
				// LOGGER.debug(DebugMarker.DEBUG1.marker, "Got ID \"$id\"")
				LOGGER.info(DebugMarker.DEBUG1.marker, "Got ID " + id)
				java.sql.DriverManager.getConnection(SqlServer.DB_CONNECTION_URI).use { connection ->
					// LOGGER.debug(DebugMarker.DEBUG1.marker, "Connected to DB \"${SqlServer.DB_CONNECTION_URI}\"")
					LOGGER.info(DebugMarker.DEBUG1.marker, "Connected to DB \"${SqlServer.DB_CONNECTION_URI}\"")
					val user = SqlServer.validateId(connection, id)
					if (user == null) {
						LOGGER.info("No user found for the scanned RFID, displaying invalid RFID message")
//						when (id) {
//							is Pin -> AlcoMeasure.displayMessage(MESSAGES_BUNDLE.getString("INVALID_PIN"))
//							is Rfid -> AlcoMeasure.displayMessage(MESSAGES_BUNDLE.getString("INVALID_RFID"))
//						}
						AlcoMeasure.displayMessage(MESSAGES_BUNDLE.getString("INVALID_RFID"))
					} else {
						// LOGGER.debug(DebugMarker.DEBUG1.marker, "User is valid, performing AlcoMeasure test")
						LOGGER.info(DebugMarker.DEBUG1.marker, "User is valid, performing AlcoMeasure test")
						// TODO Push user info to Redis with timestamp
						LOGGER.info(DebugMarker.DEBUG1.marker, "id " + id)
						// LOGGER.info(DebugMarker.DEBUG1.marker, "user.id " + user.id)

						// data class Rfid(val facilityCode: Int, val cardNumber: Int) // : Id()
						// user.id is an Rfid and not a user as such
						//
						//						 The ID and name of a user from the TouchSafe database
						//
						//						 @param id The ID of the user in the database
						//						 @param firstName The user's first name
						//						 @param surname The user's surname
						//


						// Redis.cardScanned(user.id, user.firstName, user.surname)    // user.id is not valid at the point. Probably bug here.
						Redis.cardScanned(id.cardNumber, user.firstName, user.surname)
						AlcoMeasure.performTest(user)?.let { result ->
							LOGGER.debug(DebugMarker.DEBUG1.marker, "Test completed with result $result")
							LOGGER.debug(DebugMarker.DEBUG2.marker, "Storing result")
							SqlServer.storeResult(connection, user, result)
							// TODO Push test result and email details to Redis
							if (result.value != 0.0 || mailAllReports()) {
								var subject = MESSAGES_BUNDLE.getString("EMAIL_SUBJECT")
								if (result.value != 0.0) {
									LOGGER.debug(DebugMarker.DEBUG2.marker, "Result ${result.value} is over 0.0, sending email")
									LOGGER.info(DebugMarker.DEBUG2.marker, "Result ${result.value} is over 0.0, sending email")
								} else {
									// Change email subject, as result is zero
									subject = "Breathalyser result"
									LOGGER.debug(DebugMarker.DEBUG2.marker, "mailAllReports = true, sending email")
									LOGGER.info(DebugMarker.DEBUG2.marker, "mailAllReports = true, sending email")
								}
								LOGGER.debug(DebugMarker.DEBUG2.marker, "Creating email body for breathalyser notification email")
								LOGGER.info(DebugMarker.DEBUG2.marker, "Creating email body for breathalyser notification email")
								val emailBody = java.text.MessageFormat.format(MESSAGES_BUNDLE.getString("EMAIL_BODY"), user.firstName, user.surname, "%.8f".format(result.value))
								LOGGER.debug(DebugMarker.DEBUG3.marker, "Created email body \"$emailBody\"")
								LOGGER.info(DebugMarker.DEBUG3.marker, "Created email body \"$emailBody\"")
								Email.send(Email.TO, MESSAGES_BUNDLE.getString("EMAIL_SUBJECT"), emailBody, "photo1" to result.photo1Uri, "photo2" to result.photo2Uri, "photo3" to result.photo3Uri)
								LOGGER.info("Sent email \"${MESSAGES_BUNDLE.getString("EMAIL_SUBJECT")}\" to ${Email.TO} with result value: ${"%.8f".format(result.value)} for ${user.firstName} ${user.surname}")
							}
						}
					}
				}
			} catch (ex: Throwable) {
				LOGGER.error("An unexpected error occurred:", ex)
				LOGGER.info("An unexpected error occurred:", ex)
			}
		}
	} finally {
		Input.KEYBOARD_HOOK.shutdownHook()
	}
}
