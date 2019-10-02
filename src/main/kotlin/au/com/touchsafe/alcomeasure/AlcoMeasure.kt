package au.com.touchsafe.alcomeasure

import kotlinx.coroutines.future.await

object AlcoMeasure {

	private val HOST = Input.applicationProperties.getString("alcomeasureHost")
	private val PORT = Input.applicationProperties.getString("alcomeasurePort")
	private val STATUS_URI = java.net.URI.create("http://$HOST:$PORT/status.cgi")

	private const val ERROR_STATE_TAG_START = "<ErrorState value=\""
	private const val LAST_RESULT_TAG_START = "<LastResult value=\""
	private const val OUTCOME_TAG_START = "<Outcome value=\""
	private const val PROCESS_STATE_TAG_START = "<ProcessState value=\""
	private const val TEST_STATE_TAG_START = "<TestState value=\""
	private const val XML_TAG_END = "\"/>"

	private const val POLLING_DELAY = 500L
	private const val RESULT_CONVERSION_VALUE = 44000.0

	suspend fun performTest(user: User): Result {
		println("Perform Test: $user") // TODO: Remove.
		return Result("", "", "", 0.0) // TODO: Remove.
//		val httpClient = java.net.http.HttpClient.newHttpClient()
//		val statusRequest = java.net.http.HttpRequest.newBuilder(STATUS_URI).build()
//		val statusResponseBody = httpClient.sendAsync(statusRequest, java.net.http.HttpResponse.BodyHandlers.ofString()).await().body()
//		when (val initialProcessStateValue = extractValue(statusResponseBody, PROCESS_STATE_TAG_START)) {
//			ProcessState.NONE.value -> {
//				val testRequest = java.net.http.HttpRequest.newBuilder(java.net.URI.create("http://$HOST:$PORT/status.cgi?startTest=5&ID=${user.id}")).build()
//				httpClient.sendAsync(testRequest, java.net.http.HttpResponse.BodyHandlers.ofString()).await()
//
//				var testStatusResponseBody: String
//				do {
//					kotlinx.coroutines.delay(POLLING_DELAY)
//					testStatusResponseBody = httpClient.sendAsync(statusRequest, java.net.http.HttpResponse.BodyHandlers.ofString()).await().body()
//					val processStateValue = extractValue(statusResponseBody, PROCESS_STATE_TAG_START)
//					val testStateValue = extractValue(statusResponseBody, TEST_STATE_TAG_START) // TODO: Remove.
//					println("processStateValue:$processStateValue:testStateValue:$testStateValue:") // TODO: Remove.
//				} while (processStateValue != ProcessState.NONE.value)
//
//				val testStateValue = extractValue(testStatusResponseBody, TEST_STATE_TAG_START)
//				val outcomeValue = extractValue(testStatusResponseBody, OUTCOME_TAG_START)
//				val errorStateValue = extractValue(testStatusResponseBody, ERROR_STATE_TAG_START)
//				val lastResultValue = extractValue(testStatusResponseBody, LAST_RESULT_TAG_START)
//				val result = Result(testStateValue, outcomeValue, errorStateValue, lastResultValue.toInt() / RESULT_CONVERSION_VALUE)
//				LOGGER.debug("Result: $result")
//				return result
//			}
//			else -> {
//				println("tmpProcessStateValue:$initialProcessStateValue:") // TODO: Remove.
//				TODO()
//			}
//		}
	}

	private fun extractValue(body: String, tagStart: String): String {
		val tagStartIndex = body.indexOf(tagStart)
		require(tagStartIndex >= 0) { "`tagStart` not found: $tagStart: $body" }
		val valueStartIndex = tagStartIndex + tagStart.length
		val tagEndIndex = body.indexOf(XML_TAG_END, valueStartIndex)
		require(tagStartIndex >= 0) { "`XML_TAG_END` not found after : $valueStartIndex: $body" }
		return body.substring(valueStartIndex, tagEndIndex)
	}
}

data class Result(val testState: String, val outcome: String, val errorState: String, val result: Double)

enum class ErrorState(val value: String) {
	NONE("None")
}

enum class Outcome(val value: String) {
	TEST_SUCCESSFUL("Test Successful")
}

enum class ProcessState(val value: String) {
	NONE("None")
}

enum class TestState(val value: String) {
	NONE("None")
}
