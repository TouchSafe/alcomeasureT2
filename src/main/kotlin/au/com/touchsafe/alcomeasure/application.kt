package au.com.touchsafe.alcomeasure

internal val LOGGER = org.slf4j.LoggerFactory.getLogger("AlcoMeasure Integration")

suspend fun main() {
	LOGGER.info("TouchSafe 2 AlcoMeasure Integration: STARTED")

	while (true) {
		val id = Input.getId()
		val user = SqlServer.validateId(id)
		if (user == null) {
			LOGGER.info("User not found in the database: $id")
			// TODO
		} else {
			val result = AlcoMeasure.performTest(user)
			SqlServer.storeResult(user, result)
		}
	}
}
