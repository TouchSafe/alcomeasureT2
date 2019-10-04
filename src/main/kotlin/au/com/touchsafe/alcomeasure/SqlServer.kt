package au.com.touchsafe.alcomeasure

import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle

object SqlServer {

	private const val APPLICATION_NAME = "AlcoMeasure Integration"
	private val DB_FACTORY = io.r2dbc.mssql.MssqlConnectionConfiguration.builder()
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

	suspend fun validateId(id: Id): User? {
		val connection = DB_FACTORY.create().awaitSingle()
		try {
			val result = when (id) {
				is Pin -> execute(connection, "$VALIDATE_ID_SQL_START AND pin = @pin;", "pin" to id.pin)
				is Rfid -> execute(connection, "$VALIDATE_ID_SQL_START AND rfidFacilityId = @rfidFacilityId AND rfid = @rfid;", "rfidFacilityId" to id.facilityCode, "rfid" to id.cardNumber)
			} ?: return null
			return result.map { row, _ -> User(row["id", Integer::class.java]!!.toInt(), row["firstName", String::class.java]!!, row["lastName", String::class.java]!!) }.awaitFirstOrNull()
		} catch (ex: Throwable) {
			LOGGER.error("Error occurred while validating an Id:", ex)
			return null
		} finally {
			connection.close().awaitFirstOrNull()
		}
	}

	suspend fun storeResult(user: User, result: Result) {
		// TODO:
//		val connection = dbFactory.create().awaitSingle()
//		try {
//			execute(connection, "INSERT INTO AlcomeasureResults (userId, column1, column2) VALUES (@userId, @column1, @column2);", "userId" to user.id, "column1" to result.value, "column2" to result.value)
//		} catch (ex: Throwable) {
//			LOGGER.error("Error occurred while storing results:", ex)
//		} finally {
//			connection.close().awaitFirstOrNull()
//		}
		LOGGER.info("Stored Result: $user $result")
	}

	private suspend fun execute(connection: io.r2dbc.mssql.MssqlConnection, sql: String, vararg bindings: Pair<String, Any>): io.r2dbc.mssql.MssqlResult? {
		var statement = connection.createStatement(sql)
		bindings.forEach { binding ->
			LOGGER.debug("Binding: ${binding.first} -> ${binding.second}")
			statement = statement.bind(binding.first, binding.second)
		}
		return statement.execute().awaitFirstOrNull()
	}
}

data class User(val id: Int, val firstName: String, val surname: String)
