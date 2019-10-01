package au.com.touchsafe.alcomeasure

object AlcoMeasure {

	private val host = Input.applicationPropeties.getString("dbHost")
	private val port = Input.applicationPropeties.getString("dbPort").toInt()

	suspend fun performTest(user: User) : Result {
		TODO()
	}
}

data class Result(val value: String)
