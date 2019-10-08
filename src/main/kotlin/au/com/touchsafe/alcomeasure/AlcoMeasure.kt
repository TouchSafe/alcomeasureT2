package au.com.touchsafe.alcomeasure

import kotlinx.coroutines.future.await

object AlcoMeasure {

	private val DEFAULT_DISPLAY_TIME_MS = SETTINGS_BUNDLE.getString("alcomeasureDefaultDisplayTimeMs").toInt()
	private val HOST = SETTINGS_BUNDLE.getString("alcomeasureHost")
	private val PORT = SETTINGS_BUNDLE.getString("alcomeasurePort")
	private val START_TEST_URI = java.net.URI.create("http://$HOST:$PORT/status.cgi?startTest=5")
	private val STATUS_URI = java.net.URI.create("http://$HOST:$PORT/status.cgi")

	private const val ERROR_STATE_TAG_START = "<ErrorState value=\""
	private const val LAST_RESULT_TAG_START = "<LastResult value=\""
	private const val OUTCOME_TAG_START = "<Outcome value=\""
	private const val PROCESS_STATE_TAG_START = "<ProcessState value=\""
	private const val TEST_STATE_TAG_START = "<TestState value=\""
	private const val XML_TAG_END = "\"/>"

	private val NEW_LINE_REGEX = Regex("""[\r\n]""")
	private const val START_TEST_RESPONSE = """<status.cgi><startTest><error value="None"/><success value="1"/></startTest></status.cgi>"""

	private const val POLLING_DELAY = 500L
	private const val RESULT_CONVERSION_VALUE = 44000.0

	suspend fun displayMessage(message: String, displayTimeMs: Int = DEFAULT_DISPLAY_TIME_MS) {
		LOGGER.info(message)
		// TODO: Implement custom message display on the AlcoMeasure with new API.
	}

	suspend fun performTest(user: User): Double? {
		displayMessage(java.text.MessageFormat.format(MESSAGES_BUNDLE.getString("WELCOME_PERSON"), user.firstName, user.surname))
		val httpClient = java.net.http.HttpClient.newHttpClient()
		val statusRequest = java.net.http.HttpRequest.newBuilder(STATUS_URI).build()
		val initialStatusResponseBody = httpClient.sendAsync(statusRequest, java.net.http.HttpResponse.BodyHandlers.ofString()).await().body().replace(NEW_LINE_REGEX, "")
		LOGGER.debug("Initial status response body:$initialStatusResponseBody:")
		val initialProcessStateValue = extractValue(initialStatusResponseBody, PROCESS_STATE_TAG_START)
		if (initialProcessStateValue != ProcessState.NONE.value) {
			LOGGER.error("Invalid initial process state value:$initialProcessStateValue:")
			displayMessage(MESSAGES_BUNDLE.getString("START_TEST_FAILED"))
		} else {
			val startTestRequest = java.net.http.HttpRequest.newBuilder(START_TEST_URI).build()
			val startTestResponseBody = httpClient.sendAsync(startTestRequest, java.net.http.HttpResponse.BodyHandlers.ofString()).await().body().replace(NEW_LINE_REGEX, "")
			LOGGER.debug("Start test response body:$startTestResponseBody:")
			if (startTestResponseBody != START_TEST_RESPONSE) {
				LOGGER.error("Invalid start test response body.")
				displayMessage(MESSAGES_BUNDLE.getString("START_TEST_FAILED"))
			} else {
				do {
					kotlinx.coroutines.delay(POLLING_DELAY)
					val testStatusResponseBody = httpClient.sendAsync(statusRequest, java.net.http.HttpResponse.BodyHandlers.ofString()).await().body().replace(NEW_LINE_REGEX, "")
					LOGGER.debug("Test status response body:$testStatusResponseBody:")
					val processStateValue = extractValue(testStatusResponseBody, PROCESS_STATE_TAG_START)
					val testStateValue = extractValue(testStatusResponseBody, TEST_STATE_TAG_START)
					if (processStateValue != ProcessState.NORMAL_TEST.value) {
						LOGGER.error("Process state has changed before the result has been read.")
					} else if (testStateValue == TestState.OUTCOME_NOT_RETRIEVED.value) {
						val outcomeValue = extractValue(testStatusResponseBody, OUTCOME_TAG_START)
						if (outcomeValue != Outcome.TEST_SUCCESSFUL.value) {
							LOGGER.info("Test not successful:$outcomeValue:")
						} else {
							val errorStateValue = extractValue(testStatusResponseBody, ERROR_STATE_TAG_START)
							if (errorStateValue != ErrorState.NONE.value) {
								LOGGER.info("Test error occurred:$outcomeValue:")
							} else {
								return extractValue(testStatusResponseBody, LAST_RESULT_TAG_START).toInt() / RESULT_CONVERSION_VALUE
							}
						}
					}
				} while (processStateValue == ProcessState.NORMAL_TEST.value)
			}
		}
		return null
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

enum class ErrorState(val value: String) {
	NONE("None")
}

@Suppress("unused")
enum class Outcome(val value: String) {
	BLOW_STOPPED("Blow Stopped"),
	NO_OUTCOME("No Outcome"),
	TEST_SUCCESSFUL("Test Successful")
}

enum class ProcessState(val value: String) {
	NONE("None"),
	NORMAL_TEST("Normal Test")
}

@Suppress("unused")
enum class TestState(val value: String) {
	NONE("None"),
	WAITING_FOR_BLOW_START("Waiting For Blow Start"),
	WAITING_FOR_BLOW_FINISH("Waiting For Blow Finish"),
	FINDING_RESULTS("Finding Results"),
	RECOVERING("Recovering"),
	OUTCOME_NOT_RETRIEVED("Outcome Not Retrieved")
}
