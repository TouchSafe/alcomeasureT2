package au.com.touchsafe.alcomeasure

suspend fun main() {
	println("TouchSafe 2 AlcoMeasure Integration: STARTED")

	while (true) {
		val id = Input.getId()
		val user = SqlServer.validateId(id)
		val result = AlcoMeasure.performTest(user)
		SqlServer.storeResult(user, result)
	}
}
