package au.com.touchsafe.alcomeasure

object SqlServer {

	private const val APPLICATION_NAME = "AlcoMeasure Integration"
	internal val DB_CONNECTION_URI = "jdbc:sqlserver://${SETTINGS_PROPERTIES.getProperty("dbHost")}:${SETTINGS_PROPERTIES.getProperty("dbPort")};databaseName=${SETTINGS_PROPERTIES.getProperty("dbDatabase")};user=${SETTINGS_PROPERTIES.getProperty("dbUsername")};password=${SETTINGS_PROPERTIES.getProperty("dbPassphrase")};applicationName=$APPLICATION_NAME;"
	private const val VALIDATE_ID_SQL_START = "SELECT id, firstName, lastName FROM [User] WHERE deleted = 0 AND DATEDIFF(HOUR, GETDATE(), accessExpiry) > 0"

	/**
	 * Checks the RFID data against the database, and returns the user that the card was registered to,
	 * or null if no user could be found
	 * @param connection the connection to the TouchSafe SQL Server database
	 * @param id the RFID data from the scanned card
	 * @return The User that the card was registered to, or null if no user exists with the card
	 */
	fun validateId(connection: java.sql.Connection, id: Rfid): User? {
		LOGGER.debug(DebugMarkers.DEBUG1.marker, "validateId {cardNo: ${id.cardNumber}, facilityCode: ${id.facilityCode}}")
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
			LOGGER.debug(DebugMarkers.DEBUG2.marker, "Query: $statement")
			val resultSet = statement.executeQuery()
			if (!resultSet.next()) {
				LOGGER.info("No user found for Rfid \"${id.cardNumber}\"")
				return null
			}
			LOGGER.debug("Validated User \"${resultSet.getString("firstName")} ${resultSet.getString("lastName")}\" from Rfid \"${id.cardNumber}\"")
			return User(resultSet.getInt("id"), resultSet.getString("firstName"), resultSet.getString("lastName"))
		} catch (ex: Throwable) {
			LOGGER.error("Error occurred while validating an Id:", ex)
			return null
		}
	}

	/**
	 * Stores the [Result] in the database through [connection]
	 * @param connection the connection to the TouchSafe SQL Server database
	 * @param user the user that the result belongs to
	 * @param result the result from the [AlcoMeasure] test
	 */
	fun storeResult(connection: java.sql.Connection, user: User, result: Result) {
		LOGGER.debug(DebugMarkers.DEBUG1.marker, "storeResult ${result.value} for user ${user.firstName} ${user.surname}")
		try {
			val photo1Id = result.photo1Uri?.let { downloadAndStorePhoto(connection, it) }
			val photo2Id = result.photo2Uri?.let { downloadAndStorePhoto(connection, it) }
			val photo3Id = result.photo3Uri?.let { downloadAndStorePhoto(connection, it) }
			val statement = connection.prepareStatement("INSERT INTO AlcoMeasureResult (userId, result, photo1Id, photo2Id, photo3Id) VALUES (?, ?, ?, ?, ?);")
			statement.setInt(1, user.id)
			statement.setDouble(2, result.value)
			if (photo1Id == null) statement.setNull(3, java.sql.Types.INTEGER) else statement.setInt(3, photo1Id)
			if (photo2Id == null) statement.setNull(4, java.sql.Types.INTEGER) else statement.setInt(4, photo2Id)
			if (photo3Id == null) statement.setNull(5, java.sql.Types.INTEGER) else statement.setInt(5, photo3Id)
			statement.executeUpdate()
			LOGGER.debug("Stored Result:${user.id}:$result:$photo1Id:$photo2Id:$photo3Id:")
		} catch (ex: Throwable) {
			LOGGER.error("Error occurred while storing result:", ex)
		}
	}

	/**
	 * Downloads and INSERTs the file from [photoUri] into the database through [connection]
	 * @param connection the connection to the TouchSafe SQL Server database
	 * @param photoUri the URI to the photo from the result from the [AlcoMeasure] test
	 * @return the ID of the INSERTed file
	 */
	private fun downloadAndStorePhoto(connection: java.sql.Connection, photoUri: java.net.URL): Int? {
		LOGGER.debug(DebugMarkers.DEBUG1.marker, "downloadAndStorePhoto $photoUri")
		val statement = connection.prepareStatement("INSERT INTO [File] (fileStreamId, fileContent) OUTPUT INSERTED.ID VALUES (NEWID(), ?);")
		statement.setBlob(1, photoUri.openStream())
		val resultSet = statement.executeQuery()
		if (!resultSet.next()) {
			LOGGER.debug("No result from query")
			return null
		}
		return resultSet.getInt(1)
	}
}

/**
 * The ID and name of a user from the TouchSafe database
 *
 * @param id the ID of the user in the database
 * @param firstName the user's first name
 * @param surname the user's surname
 */
data class User(val id: Int, val firstName: String, val surname: String)
