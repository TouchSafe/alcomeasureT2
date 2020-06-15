package au.com.touchsafe.alcomeasure

object AlcoMeasure {

	private val HOST = SETTINGS_PROPERTIES.getProperty("alcomeasureHost")
	private val PORT = SETTINGS_PROPERTIES.getProperty("alcomeasurePort")
	private val DISPLAY_MESSAGE_URI_ADDRESS = "http://$HOST:$PORT/status.cgi?display=" // Followed by an encoded message to scroll on the screen once.

	// NOTE: Fixed display for a time: URI: "http://$HOST:$PORT/status.cgi?display=${encodedFixedMessage}&time=${displayForSeconds}" // The fixed message can only be up to 17 characters!
	private val DOWNLOAD_LOG_URI_ADDRESS = "http://$HOST:$PORT/log.cgi?downloadInternal&initial={0}&size={1}"
	private val DOWNLOAD_PHOTO_URI_ADDRESS = "http://$HOST:$PORT/log.cgi?downloadPhoto&photoNo="

	// private val START_TEST_URI = java.net.URI.create("http://$HOST:$PORT/status.cgi?startTest=5")
	private val START_TEST_URI_ADDRESS = "http://$HOST:$PORT/status.cgi?startTest=5&ID={0}&display="
	private val STATUS_URI = java.net.URI.create("http://$HOST:$PORT/status.cgi")

	private const val ERROR_STATE_TAG_START = "<ErrorState value=\""
	private const val LAST_LOG_NUMBER_TAG_START = "<LastLogNo value=\""
	private const val LAST_RESULT_TAG_START = "<LastResult value=\""
	private const val OUTCOME_TAG_START = "<Outcome value=\""
	private const val PROCESS_STATE_TAG_START = "<ProcessState value=\""
	private const val TEST_STATE_TAG_START = "<TestState value=\""
	private const val XML_TAG_END = "\"/>"

	private const val DISPLAY_MESSAGE_RESPONSE = """<status.cgi><display><success value="1"/></display></status.cgi>"""
	private val NEW_LINE_REGEX = Regex("""[\r\n]""")
	private val IMAGE_NUMBER_REGEX = Regex("""IM(\d+).JPG""")
	private const val START_TEST_RESPONSE = """<status.cgi><startTest><success value="1"/></startTest></status.cgi>"""

	private const val POLLING_DELAY = 500L
	private const val RESULT_CONVERSION_VALUE = 44000.0

	fun displayMessage(message: String) {
		LOGGER.info("Display message:$message:")
		val httpClient = java.net.http.HttpClient.newHttpClient()
		val statusRequest = java.net.http.HttpRequest.newBuilder(java.net.URI.create(DISPLAY_MESSAGE_URI_ADDRESS + java.net.URLEncoder.encode(message, java.nio.charset.StandardCharsets.UTF_8))).build()
		val displayMessageResponseBody = httpClient.send(statusRequest, java.net.http.HttpResponse.BodyHandlers.ofString()).body().replace(NEW_LINE_REGEX, "")
		if (displayMessageResponseBody != DISPLAY_MESSAGE_RESPONSE) {
			LOGGER.error("Display message response body:$displayMessageResponseBody:")
		}
	}

	fun performTest(user: User): Result? {
		val httpClient = java.net.http.HttpClient.newHttpClient()
		val statusRequest = java.net.http.HttpRequest.newBuilder(STATUS_URI).build()
		val initialStatusResponseBody = httpClient.send(statusRequest, java.net.http.HttpResponse.BodyHandlers.ofString()).body().replace(NEW_LINE_REGEX, "")
		LOGGER.debug("Initial status response body:$initialStatusResponseBody:")
		val initialProcessStateValue = extractValue(initialStatusResponseBody, PROCESS_STATE_TAG_START)
		if (initialProcessStateValue != ProcessState.NONE.value) {
			LOGGER.error("Invalid initial process state value:$initialProcessStateValue:")
			displayMessage(MESSAGES_BUNDLE.getString("START_TEST_FAILED"))
			return null
		}
		val startTestUri = java.net.URI.create(java.text.MessageFormat.format(START_TEST_URI_ADDRESS, user.id.toString()) + java.net.URLEncoder.encode(java.text.MessageFormat.format(MESSAGES_BUNDLE.getString("WELCOME_PERSON"), user.firstName, user.surname), java.nio.charset.StandardCharsets.UTF_8))
		val startTestRequest = java.net.http.HttpRequest.newBuilder(startTestUri).build()
		val startTestResponseBody = httpClient.send(startTestRequest, java.net.http.HttpResponse.BodyHandlers.ofString()).body().replace(NEW_LINE_REGEX, "")
		LOGGER.debug("Start test response body:$startTestResponseBody:")
		if (startTestResponseBody != START_TEST_RESPONSE) {
			LOGGER.error("Invalid start test response body.")
			displayMessage(MESSAGES_BUNDLE.getString("START_TEST_FAILED"))
			return null
		}
		while (true) {
			Thread.sleep(POLLING_DELAY)
			val testStatusResponseBody = httpClient.send(statusRequest, java.net.http.HttpResponse.BodyHandlers.ofString()).body().replace(NEW_LINE_REGEX, "")
			LOGGER.debug("Test status response body:$testStatusResponseBody:")
			val processStateValue = extractValue(testStatusResponseBody, PROCESS_STATE_TAG_START)
			val testStateValue = extractValue(testStatusResponseBody, TEST_STATE_TAG_START)
			if (processStateValue != ProcessState.NORMAL_TEST.value) {
				LOGGER.error("Process state has changed before the result has been read.")
				return null
			}
			if (testStateValue == TestState.OUTCOME_NOT_RETRIEVED.value) {
				val outcomeValue = extractValue(testStatusResponseBody, OUTCOME_TAG_START)
				if (outcomeValue != Outcome.TEST_SUCCESSFUL.value) {
					LOGGER.info("Test not successful:$outcomeValue:")
					return null
				}
				val errorStateValue = extractValue(testStatusResponseBody, ERROR_STATE_TAG_START)
				if (errorStateValue != ErrorState.NONE.value) {
					LOGGER.info("Test error occurred:$outcomeValue:")
					return null
				}
				val lastLogNumber = extractValue(testStatusResponseBody, LAST_LOG_NUMBER_TAG_START)
				val downloadLogUri = java.net.URI.create(java.text.MessageFormat.format(DOWNLOAD_LOG_URI_ADDRESS, lastLogNumber, 1))
				val downloadLogRequest = java.net.http.HttpRequest.newBuilder(downloadLogUri).build()
				val downloadLogResponseBody = httpClient.send(downloadLogRequest, java.net.http.HttpResponse.BodyHandlers.ofString()).body().replace(NEW_LINE_REGEX, "")
				val downloadLogParts = downloadLogResponseBody.split(',')
				if (downloadLogParts.size != 11) {
					LOGGER.error("Downloaded log does not have the correct number of parts:$downloadLogResponseBody:")
					return null
				}
				val resultValue = downloadLogParts[5].toDouble() / RESULT_CONVERSION_VALUE
				val photo1Number = IMAGE_NUMBER_REGEX.matchEntire(downloadLogParts[8])?.groups?.get(1)?.value
				val photo2Number = IMAGE_NUMBER_REGEX.matchEntire(downloadLogParts[9])?.groups?.get(1)?.value
				val photo3Number = IMAGE_NUMBER_REGEX.matchEntire(downloadLogParts[10])?.groups?.get(1)?.value
				val result = Result(resultValue, photo1Number?.let { java.net.URL(DOWNLOAD_PHOTO_URI_ADDRESS + it) }, photo2Number?.let { java.net.URL(DOWNLOAD_PHOTO_URI_ADDRESS + it) }, photo3Number?.let { java.net.URL(DOWNLOAD_PHOTO_URI_ADDRESS + it) })
				LOGGER.info("Result retrieved: $result")
				return result
			}
		}
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

data class Result(val value: Double, val photo1Uri: java.net.URL?, val photo2Uri: java.net.URL?, val photo3Uri: java.net.URL?)

@Suppress("unused")
enum class TestState(val value: String) {
	NONE("None"),
	WAITING_FOR_BLOW_START("Waiting For Blow Start"),
	WAITING_FOR_BLOW_FINISH("Waiting For Blow Finish"),
	FINDING_RESULTS("Finding Results"),
	RECOVERING("Recovering"),
	OUTCOME_NOT_RETRIEVED("Outcome Not Retrieved")
}
