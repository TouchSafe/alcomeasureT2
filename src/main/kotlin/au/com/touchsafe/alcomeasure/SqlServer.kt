package au.com.touchsafe.alcomeasure

import kotlinx.coroutines.reactive.awaitFirstOrNull

object SqlServer {

	private const val APPLICATION_NAME = "AlcoMeasure Integration"
	internal val DB_FACTORY = io.r2dbc.mssql.MssqlConnectionConfiguration.builder()
			.applicationName(APPLICATION_NAME)
			.host(SETTINGS_BUNDLE.getString("dbHost"))
			.port(SETTINGS_BUNDLE.getString("dbPort").toInt())
			.database(SETTINGS_BUNDLE.getString("dbDatabase"))
			.username(SETTINGS_BUNDLE.getString("dbUsername"))
			.password(SETTINGS_BUNDLE.getString("dbPassphrase"))
			.enableSsl()
			.build()
			.let {
				val dbFactory = io.r2dbc.mssql.MssqlConnectionFactory(it)
				LOGGER.debug("Database connection factory constructed: $dbFactory")
				dbFactory
			}
	private const val VALIDATE_ID_SQL_START = "SELECT id, firstName, lastName FROM [User] WHERE deleted = 0 AND DATEDIFF(HOUR, GETDATE(), accessExpiry) > 0"

	suspend fun validateId(connection: io.r2dbc.mssql.MssqlConnection, id: Id): User? {
		try {
			val result = when (id) {
				is Pin -> execute(connection, "$VALIDATE_ID_SQL_START AND pin = @pin;", "pin" to id.pin)
				is Rfid -> execute(connection, "$VALIDATE_ID_SQL_START AND rfidFacilityId = @rfidFacilityId AND rfid = @rfid;", "rfidFacilityId" to id.facilityCode, "rfid" to id.cardNumber)
			} ?: return null
			return result.map { row, _ -> User(row["id", Integer::class.java]!!.toInt(), row["firstName", String::class.java]!!, row["lastName", String::class.java]!!) }.awaitFirstOrNull()
		} catch (ex: Throwable) {
			LOGGER.error("Error occurred while validating an Id:", ex)
			return null
		}
	}

	suspend fun storeResult(connection: io.r2dbc.mssql.MssqlConnection, user: User, result: Double) {
		try {
			val photo1Id = Integer::class.java // downloadAndStorePhoto(connection) ?: Integer::class.java
			val photo2Id = Integer::class.java // downloadAndStorePhoto(connection) ?: Integer::class.java
			val photo3Id = Integer::class.java // downloadAndStorePhoto(connection) ?: Integer::class.java
			execute(connection, "INSERT INTO AlcoMeasureResults (userId, result, photo1, photo2, photo3) VALUES (@userId, @result, @photo1, @photo2, @photo3);", "userId" to user.id, "result" to result, "photo1" to photo1Id, "photo2" to photo2Id, "photo3" to photo3Id)
			LOGGER.debug("Stored Result:$user:$result:$photo1Id:$photo2Id:$photo3Id:")
		} catch (ex: Throwable) {
			LOGGER.error("Error occurred while storing result:", ex)
		}
	}

	private suspend fun downloadAndStorePhoto(connection: io.r2dbc.mssql.MssqlConnection): Int? {
		// TODO: Download the photo from the AlcoMeasure.
		val fileContent = java.nio.ByteBuffer.wrap(java.io.File("""C:\old\tmpPhoto.jpg""").readBytes())
		val result = execute(connection, """INSERT INTO File (fileStreamId, fileContent) OUTPUT id VALUES (NEWID(), @fileContent);""", "fileContent" to fileContent) ?: return null
		// NOTE: While this may be able to be made to work, how would we get the file saved onto the server to be imported?
		//	val photoFile = java.io.File("""C:\old\tmpPhoto.jpg""")
		//	val result = execute(connection, """INSERT INTO [File] (fileStreamId, fileContent) OUTPUT Inserted.ID VALUES (NEWID(), (SELECT * FROM OPENROWSET(BULK N'${photoFile.absolutePath}', SINGLE_BLOB) AS Image001));""") ?: return null
		return result.map { row, _ -> row[1, Integer::class.java]!!.toInt() }.awaitFirstOrNull()
	}

	private suspend fun execute(connection: io.r2dbc.mssql.MssqlConnection, sql: String, vararg bindings: Pair<String, Any>): io.r2dbc.mssql.MssqlResult? {
		var statement = connection.createStatement(sql)
		bindings.forEach { binding ->
			LOGGER.debug("Binding: ${binding.first} -> ${binding.second}")
			statement = if (binding.second is Class<*>) statement.bindNull(binding.first, binding.second as Class<*>) else statement.bind(binding.first, binding.second)
		}
		return statement.execute().awaitFirstOrNull()
	}
}

data class User(val id: Int, val firstName: String, val surname: String)
