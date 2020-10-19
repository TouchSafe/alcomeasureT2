package au.com.touchsafe.alcomeasure

import au.com.touchsafe.alcomeasure.util.*
import au.com.touchsafe.alcomeasure.util.logging.DebugMarker
import org.apache.commons.lang3.SystemUtils
import org.jnativehook.GlobalScreen
import java.util.logging.Level
import java.util.logging.Logger
import kotlin.system.exitProcess

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
	// Disable JNativeHook logging
	Logger.getLogger(GlobalScreen::class.java.`package`.name).level = Level.OFF

	println("TouchSafe 2 AlcoMeasure Integration: STARTED")
	LOGGER.info("TouchSafe 2 AlcoMeasure Integration: STARTED")   // TODO output the version number here

	// Settings that are required for the program to run correctly
	val requiredSettingsProperties = listOf(
			// Redis settings
			RequiredSettingsProperty("applicationID"),
			RequiredSettingsProperty("redisServer"),
			RequiredSettingsProperty("redisPort"),

			// AlcoMeasure device details - required to connect to the device
			RequiredSettingsProperty("alcomeasureHost"),
			RequiredSettingsProperty("alcomeasurePort"),
			// LocationId for the AlcoMeasure unit in the AlcoMeasureDevice table
			RequiredSettingsProperty("alcomeasureLocationId", regex = Regex("^[0-9]+$")),

			// Database settings - these are all required to create the connection uri
			RequiredSettingsProperty("dbHost"),
			RequiredSettingsProperty("dbPort"),
			RequiredSettingsProperty("dbDatabase"),
			RequiredSettingsProperty("dbUsername"),
			RequiredSettingsProperty("dbPassphrase"),

			// These settings are required to send emails - should we make email sending optional?
			RequiredSettingsProperty("emailAuth"),
			RequiredSettingsProperty("emailFrom"),
			RequiredSettingsProperty("emailHost"),
			RequiredSettingsProperty("emailPassphrase"),
			RequiredSettingsProperty("emailPort"),
			RequiredSettingsProperty("emailStartTls"),
			RequiredSettingsProperty("emailTo"),
			RequiredSettingsProperty("emailUsername")
	)

	// Check that the required settings are present and that they match the required regex - ".*" by default
	// checkAllPresent not required, as checkAllMatchRegex checks that the setting is present
	if (!requiredSettingsProperties.checkAllMatchRegex()) {
		exitProcess(1)
	}

	val os = getOperatingSystemSystemUtils()
	LOGGER.info("OS: $os")
	if (os != "Linux") {
		LOGGER.info("Connected keyboards:" + lc.kra.system.keyboard.GlobalKeyboardHook.listKeyboards().map { (key, value) -> " [$key:$value]" }.joinToString(""))
	} else {
		LOGGER.debug("Registering JNativeHook native hook")
		GlobalScreen.registerNativeHook()
		LOGGER.debug("Registered JNativeHook native hook")
		LOGGER.debug("Adding JNativeHook key listener")
		GlobalScreen.addNativeKeyListener(InputV2.KEYBOARD_HOOK)
		LOGGER.info("Added JNativeHook key listener")
	}
	// TODO Need to handle connection error here with Redis
	Redis.applicationStarted()

	// TODO Add a database connection on program start. See TSALMT2-26

	try {
		LOGGER.debug(DebugMarker.DEBUG1.marker, "Starting main loop")
		while (true) {
			try {

				val id: Rfid
				if (os != "Linux") {
					LOGGER.info("os: $os")
					id = Input.getId()
				} else {
					LOGGER.info("Running on Linux: getting input from JNativeHook")
					val possibleId = InputV2.getId()
					if (possibleId == null) {
						LOGGER.info("Invalid RFID processed, displaying message on AlcoMeasure unit and skipping loop")
						// Show "Invalid RFID" message on AlcoMeasure unit and skip loop
						AlcoMeasure.displayMessage(MESSAGES_BUNDLE.getString("INVALID_RFID"))
						continue
					}
					id = possibleId
					// exitProcess(1)
				}

				// LOGGER.debug(DebugMarker.DEBUG1.marker, "Got ID \"$id\"")
				LOGGER.info(DebugMarker.DEBUG1.marker, "Got ID $id")
				// TODO database connection error is not being handled here
				LOGGER.debug(DebugMarker.DEBUG1.marker, "DB URI: " + SqlServer.DB_CONNECTION_URI)
				java.sql.DriverManager.getConnection(SqlServer.DB_CONNECTION_URI).use { connection ->
					// LOGGER.debug(DebugMarker.DEBUG1.marker, "Connected to DB \"${SqlServer.DB_CONNECTION_URI}\"")
					LOGGER.info(DebugMarker.DEBUG1.marker, "Connected to DB \"${SqlServer.DB_CONNECTION_URI}\"")

					// TODO Load information from the AlcoMeasureDevice table and store somewhere
					val alcoDevice = SqlServer.validateAlcoMeasureDevice(connection, SETTINGS_PROPERTIES.getProperty("alcomeasureLocationId").toInt())

					if (alcoDevice == null) {
						LOGGER.info("No Alco Measure Device found for this location. Unable to run a test at this location")

					} else {

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
							LOGGER.info(DebugMarker.DEBUG1.marker, "id $id")
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
							// TODO handle Linux / Dev system here
							if (os != "Linux") {
								AlcoMeasure.performTest(user)?.let { result ->
									LOGGER.debug(DebugMarker.DEBUG1.marker, "Test completed with result $result")
									LOGGER.debug(DebugMarker.DEBUG2.marker, "Storing result")
									SqlServer.storeResult(connection, user, result)
									// TODO Push test result and email details to Redis
									if (result.value != 0.0 || mailAllReports()) {
										val subject: String
										if (result.value != 0.0) {
											subject = MESSAGES_BUNDLE.getString("EMAIL_SUBJECT_NONZERO")
											LOGGER.debug(DebugMarker.DEBUG2.marker, "Result ${result.value} is over 0.0, sending email")
											LOGGER.info("Result ${result.value} is over 0.0, sending email")
										} else {
											subject = MESSAGES_BUNDLE.getString("EMAIL_SUBJECT_ZERO")
											LOGGER.debug(DebugMarker.DEBUG2.marker, "mailAllReports = true, sending email")
											LOGGER.info("mailAllReports = true, sending email")
										}
										LOGGER.debug(DebugMarker.DEBUG2.marker, "Creating email body for breathalyser notification email")
										LOGGER.info("Creating email body for breathalyser notification email")
										val emailBody = java.text.MessageFormat.format(MESSAGES_BUNDLE.getString("EMAIL_BODY"), user.firstName, user.surname, "%.8f".format(result.value))
										LOGGER.debug(DebugMarker.DEBUG3.marker, "Created email body \"$emailBody\"")
										LOGGER.info("Created email body \"$emailBody\"")
										Email.send(Email.TO, subject, emailBody, "photo1" to result.photo1Uri, "photo2" to result.photo2Uri, "photo3" to result.photo3Uri)
										LOGGER.info("Sent email \"$subject\" to ${Email.TO} with result value: ${"%.8f".format(result.value)} for ${user.firstName} ${user.surname}")
									}
								}
							} else {
								LOGGER.info("Running on Linux: can't handle AlcoMeasure Connection currently, using a dummy result for code testing")
								// data class Result(val value: Double, val photo1Uri: java.net.URL? = null, val photo2Uri: java.net.URL? = null, val photo3Uri: java.net.URL? = null)
								val result = Result(0.00)
								LOGGER.debug(DebugMarker.DEBUG1.marker, "Test completed with result $result")
								LOGGER.debug(DebugMarker.DEBUG2.marker, "Storing result")
								SqlServer.storeResult(connection, user, result)
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
		if (os != "Linux") {
			Input.KEYBOARD_HOOK.shutdownHook()
		} else {
			GlobalScreen.unregisterNativeHook()
		}
	}
}
