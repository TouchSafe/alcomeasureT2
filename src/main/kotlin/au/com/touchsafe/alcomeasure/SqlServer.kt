package au.com.touchsafe.alcomeasure

object SqlServer {

	private const val APPLICATION_NAME = "AlcoMeasure Integration"
	internal val DB_CONNECTION_URI = "jdbc:sqlserver://${SETTINGS_BUNDLE.getString("dbHost")}:${SETTINGS_BUNDLE.getString("dbPort")};databaseName=${SETTINGS_BUNDLE.getString("dbDatabase")};user=${SETTINGS_BUNDLE.getString("dbUsername")};password=${SETTINGS_BUNDLE.getString("dbPassphrase")};applicationName=$APPLICATION_NAME;"
	private const val VALIDATE_ID_SQL_START = "SELECT id, firstName, lastName FROM [User] WHERE deleted = 0 AND DATEDIFF(HOUR, GETDATE(), accessExpiry) > 0"

	fun validateId(connection: java.sql.Connection, id: Id): User? {
		try {
			val resultSet = when (id) {
				is Pin -> {
					val statement = connection.prepareStatement("$VALIDATE_ID_SQL_START AND pin = ?;")
					statement.setString(1, id.pin)
					statement.executeQuery()
				}
				is Rfid -> {
					val statement = connection.prepareStatement("$VALIDATE_ID_SQL_START AND rfidFacilityId = ? AND rfid = ?;")
					statement.setInt(1, id.facilityCode)
					statement.setInt(2, id.cardNumber)
					statement.executeQuery()
				}
			}
			if (!resultSet.next()) return null
			return User(resultSet.getInt("id"), resultSet.getString("firstName"), resultSet.getString("lastName"))
		} catch (ex: Throwable) {
			LOGGER.error("Error occurred while validating an Id:", ex)
			return null
		}
	}

	fun storeResult(connection: java.sql.Connection, user: User, result: Result) {
		try {
			val photo1Id = result.photo1Uri?.let { downloadAndStorePhoto(connection, it) }
			val photo2Id = result.photo2Uri?.let { downloadAndStorePhoto(connection, it) }
			val photo3Id = result.photo3Uri?.let { downloadAndStorePhoto(connection, it) }
			val statement = connection.prepareStatement("INSERT INTO AlcoMeasureResults (userId, result, photo1, photo2, photo3) VALUES (?, ?, ?, ?, ?);")
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

	private fun downloadAndStorePhoto(connection: java.sql.Connection, photoUri: java.net.URL): Int? {
		val statement = connection.prepareStatement("INSERT INTO [File] (fileStreamId, fileContent) OUTPUT INSERTED.ID VALUES (NEWID(), ?);")
		statement.setBlob(1, photoUri.openStream())
		val resultSet = statement.executeQuery()
		if (!resultSet.next()) return null
		return resultSet.getInt(1)
	}
}

data class User(val id: Int, val firstName: String, val surname: String)
