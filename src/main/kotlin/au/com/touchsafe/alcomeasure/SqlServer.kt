package au.com.touchsafe.alcomeasure

import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle

object SqlServer {

	private val DB_FACTORY = io.r2dbc.mssql.MssqlConnectionConfiguration.builder()
			.host(Input.applicationProperties.getString("dbHost"))
			.port(Input.applicationProperties.getString("dbPort").toInt())
			.database(Input.applicationProperties.getString("dbDatabase"))
			.username(Input.applicationProperties.getString("dbUsername"))
			.password(Input.applicationProperties.getString("dbPassphrase"))
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
				is Rfid -> execute(connection, "$VALIDATE_ID_SQL_START AND rfidFacilityId = @rfidFacilityId AND rfid = @rfid;", "rfid" to id.cardNumber, "rfidFacilityId" to id.facilityCode)
				is Pin -> execute(connection, "$VALIDATE_ID_SQL_START AND pin = @pin;", "pin" to id.pin)
			} ?: return null
			return result.map { row, _ -> User(row["id", Int::class.java]!!, row["firstName", String::class.java]!!, row["lastName", String::class.java]!!) }.awaitSingle()
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
		LOGGER.debug("----------------------------------------------------------------------------------------------------\nExecuting SQL: $sql")
		bindings.forEach { binding ->
			statement = statement.bind(binding.first, binding.second)
			LOGGER.debug("Binding: ${binding.first} -> ${binding.second}")
		}
		return statement.execute().awaitFirstOrNull()
	}
}

data class User(val id: Int, val firstName: String, val surname: String)
