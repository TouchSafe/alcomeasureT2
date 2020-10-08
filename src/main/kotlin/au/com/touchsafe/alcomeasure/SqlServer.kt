package au.com.touchsafe.alcomeasure

import au.com.touchsafe.alcomeasure.util.logging.DebugMarker

object SqlServer {

	private const val APPLICATION_NAME = "AlcoMeasure Integration"
	internal val DB_CONNECTION_URI = "jdbc:sqlserver://${SETTINGS_PROPERTIES.getProperty("dbHost")}:${SETTINGS_PROPERTIES.getProperty("dbPort")};databaseName=${SETTINGS_PROPERTIES.getProperty("dbDatabase")};user=${SETTINGS_PROPERTIES.getProperty("dbUsername")};password=${SETTINGS_PROPERTIES.getProperty("dbPassphrase")};applicationName=$APPLICATION_NAME;"
	private const val VALIDATE_ID_SQL_START = "SELECT id, firstName, lastName FROM [User] WHERE deleted = 0 AND DATEDIFF(HOUR, GETDATE(), accessExpiry) > 0"
	private const val VALIDATE_ALCODEVICE_SQL_START = "SELECT id, LocationId FROM [AlcoMeasureDevice]"

	/**
	 * Checks the RFID data against the database, and returns the user that the card was registered to,
	 * or null if no user could be found
	 * @param connection The connection to the TouchSafe SQL Server database
	 * @param id The RFID data from the scanned card
	 * @return The User that the card was registered to, or null if no user exists with the card
	 */
	fun validateId(connection: java.sql.Connection, id: Rfid): User? {
		LOGGER.debug(DebugMarker.DEBUG1.marker, "validateId {cardNo: ${id.cardNumber}, facilityCode: ${id.facilityCode}}")
		LOGGER.info(DebugMarker.DEBUG1.marker, "validateId {cardNo: ${id.cardNumber}, facilityCode: ${id.facilityCode}}")
		try {
//			val statement = when (id) {
//				is Pin -> connection.prepareStatement("$VALIDATE_ID_SQL_START AND pin = ?;").apply {
//					setString(1, id.pin)
//				}
//				is Rfid -> connection.prepareStatement("$VALIDATE_ID_SQL_START AND rfidFacilityId = ? AND rfid = ?;").apply {
//					setInt(1, id.facilityCode)
//					setInt(2, id.cardNumber)
//				}
//			}
			val statement = connection.prepareStatement("$VALIDATE_ID_SQL_START AND rfidFacilityId = ? AND rfid = ?;")
			statement.setInt(1, id.facilityCode)
			statement.setInt(2, id.cardNumber)
			// LOGGER.debug(DebugMarker.DEBUG2.marker, "Query: $statement")
			LOGGER.info(DebugMarker.DEBUG2.marker, "Query: $statement")
			val resultSet = statement.executeQuery()
			if (!resultSet.next()) {
				LOGGER.info("No user found for Rfid \"${id.cardNumber}\"")
				return null
			}
			LOGGER.debug("Validated User \"${resultSet.getString("firstName")} ${resultSet.getString("lastName")}\" from Rfid \"${id.cardNumber}\"")
			LOGGER.info("Validated User \"${resultSet.getString("firstName")} ${resultSet.getString("lastName")}\" from Rfid \"${id.cardNumber}\"")
			return User(resultSet.getInt("id"), resultSet.getString("firstName"), resultSet.getString("lastName"))
		} catch (ex: Throwable) {
			LOGGER.error("Error occurred while validating an Id:", ex)
			LOGGER.info("Error occurred while validating an Id:", ex)
			return null
		}
	}

	/**
	 * Stores the [Result] in the database through [connection]
	 * @param connection The connection to the TouchSafe SQL Server database
	 * @param user The user that the result belongs to
	 * @param result The result from the [AlcoMeasure] test
	 */
	// TODO Add extra fields for the AlcoMeasureResult Table
	fun storeResult(connection: java.sql.Connection, user: User, result: Result) {
		LOGGER.debug(DebugMarker.DEBUG1.marker, "storeResult ${result.value} for user ${user.firstName} ${user.surname}")
		try {
			val photo1Id = result.photo1Uri?.let { downloadAndStorePhoto(connection, it) }
			val photo2Id = result.photo2Uri?.let { downloadAndStorePhoto(connection, it) }
			val photo3Id = result.photo3Uri?.let { downloadAndStorePhoto(connection, it) }
			val statement = connection.prepareStatement("INSERT INTO AlcoMeasureResult (userId, result, photo1Id, photo2Id, photo3Id, alcoMeasureDeviceId) VALUES (?, ?, ?, ?, ?, ?);")
			statement.setInt(1, user.id)
			statement.setDouble(2, result.value)
			if (photo1Id == null) statement.setNull(3, java.sql.Types.INTEGER) else statement.setInt(3, photo1Id)
			if (photo2Id == null) statement.setNull(4, java.sql.Types.INTEGER) else statement.setInt(4, photo2Id)
			if (photo3Id == null) statement.setNull(5, java.sql.Types.INTEGER) else statement.setInt(5, photo3Id)

			statement.setInt(6, 1)		// TODO AlcoMeasureDeviceID needs to be set to actual ID - this is VERY hard-coded at the moment

			statement.executeUpdate()
			LOGGER.debug("Stored Result:${user.id}:$result:$photo1Id:$photo2Id:$photo3Id:")
		} catch (ex: Throwable) {
			LOGGER.error("Error occurred while storing result:", ex)
		}
	}

	/**
	 * Downloads and INSERTs the file from [photoUri] into the database through [connection]
	 * @param connection The connection to the TouchSafe SQL Server database
	 * @param photoUri The URI to the photo from the result from the [AlcoMeasure] test
	 * @return The ID of the INSERTed file
	 */
	private fun downloadAndStorePhoto(connection: java.sql.Connection, photoUri: java.net.URL): Int? {
		LOGGER.debug(DebugMarker.DEBUG1.marker, "downloadAndStorePhoto $photoUri")
		val statement = connection.prepareStatement("INSERT INTO [File] (fileStreamId, fileContent) OUTPUT INSERTED.ID VALUES (NEWID(), ?);")
		statement.setBlob(1, photoUri.openStream())
		val resultSet = statement.executeQuery()
		if (!resultSet.next()) {
			LOGGER.debug("No result from query")
			return null
		}
		return resultSet.getInt(1)
	}

	fun validateAlcoMeasureDevice(connection: java.sql.Connection, locationId: Int): AlcoMeasureDevice? {
		LOGGER.debug("validateAlcoMeasureDevice")
		try {
			val statement = connection.prepareStatement("$VALIDATE_ALCODEVICE_SQL_START WHERE LocationId = ?;")			// TODO run an check on this LocationId query when the application starts up
			statement.setInt(1, locationId)
			LOGGER.info(DebugMarker.DEBUG2.marker, "Query: $statement")
			val resultSet = statement.executeQuery()
			if (!resultSet.next()) {
				LOGGER.info("No Alco Mesaure Device found for LocationId \"${locationId}\"")
				return null
			}
			return AlcoMeasureDevice(resultSet.getInt("id"), resultSet.getInt("LocationId"))
		} catch (ex: Throwable) {
			LOGGER.error("Error occurred while validating an AlcoMeasureDevice Id:", ex)
			LOGGER.info("Error occurred while validating an AlcoMeasureDevice Id:", ex)
			return null
		}
	}

}

/**
 * The ID and name of a user from the TouchSafe database
 *
 * @param id The ID of the user in the database
 * @param firstName The user's first name
 * @param surname The user's surname
 */
data class User(val id: Int, val firstName: String, val surname: String)

/**
 * TODO document function and params
 */
data class AlcoMeasureDevice(val id: Int, val locationId: Int)

