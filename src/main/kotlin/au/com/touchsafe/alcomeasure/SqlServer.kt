package au.com.touchsafe.alcomeasure

import kotlinx.coroutines.reactive.awaitFirstOrNull
import kotlinx.coroutines.reactive.awaitSingle
import reactor.core.publisher.toMono

object SqlServer {

	private val dbFactory = io.r2dbc.mssql.MssqlConnectionConfiguration.builder()
			.host(Input.applicationPropeties.getString("dbHost"))
			.port(Input.applicationPropeties.getString("dbPort").toInt())
			.database(Input.applicationPropeties.getString("dbDatabase"))
			.username(Input.applicationPropeties.getString("dbUsername"))
			.password(Input.applicationPropeties.getString("dbPassword"))
			.build()
			.let { io.r2dbc.mssql.MssqlConnectionFactory(it) }

	suspend fun validateId(id: Id): User? {
		val connection = dbFactory.create().awaitSingle()
		val result = when (id) {
			is Rfid -> connection.createStatement("SELECT id, firstName, lastName FROM [User] WHERE rfid = @rfid AND rfidFacilityId = @rfidFacilityId;").bind("rfid", id.cardNumber).bind("rfidFacilityId", id.facilityCode)
			is Pin -> connection.createStatement("SELECT id, firstName, lastName FROM [User] WHERE pin = @pin;").bind("pin", id.pin)
		}.execute().awaitFirstOrNull() ?: return null
		return result.map{ row, _ -> User(row["id", Int::class.java]!!, row["firstName", String::class.java]!!, row["lastName", String::class.java]!!) }.toMono().awaitSingle()
	}

	suspend fun storeResult(user: User, result: Result) {
		TODO()
	}
}

data class User(val id: Int, val firstName: String, val surname: String)
