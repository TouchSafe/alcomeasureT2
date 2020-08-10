package au.com.touchsafe.alcomeasure

import au.com.touchsafe.alcomeasure.util.logging.DebugMarker

object Email {

	private val FROM = SETTINGS_PROPERTIES.getProperty("emailFrom")

	/**
	 * Session created with values from [SETTINGS_PROPERTIES]
	 *
	 * Uses the following properties:
	 * - emailAuth - value for the session's mail.smtp.auth property
	 * - emailStartTls - value for the session's mail.smtp.starttls.enable property
	 * - emailHost - SMTP host that the session connects to
	 * - emailPort - SMTP port that the session connects to
	 * - emailUsername - username used by the session for authentication
	 * - emailPassphrase - password used by the session for authentication
	 */
	private val SESSION = java.util.Properties().let { properties ->
		properties["mail.smtp.auth"] = SETTINGS_PROPERTIES.getProperty("emailAuth")
		properties["mail.smtp.starttls.enable"] = SETTINGS_PROPERTIES.getProperty("emailStartTls")
		properties["mail.smtp.host"] = SETTINGS_PROPERTIES.getProperty("emailHost")
		properties["mail.smtp.port"] = SETTINGS_PROPERTIES.getProperty("emailPort")
		if (LOGGER.isDebugEnabled) {
			var logStr = "Creating mail session with properties:"
			properties.stringPropertyNames().forEach { propertyName ->
				logStr += "\n\t$propertyName: ${properties.getProperty(propertyName)}"
			}
			logStr += "\n\tusername: ${SETTINGS_PROPERTIES.getProperty("emailUsername")}" +
					"\n\tpassphrase: ${SETTINGS_PROPERTIES.getProperty("emailPassphrase")}"
			LOGGER.debug(logStr)
		}
		javax.mail.Session.getInstance(properties, object : javax.mail.Authenticator() {
			override fun getPasswordAuthentication() = javax.mail.PasswordAuthentication(SETTINGS_PROPERTIES.getProperty("emailUsername"), SETTINGS_PROPERTIES.getProperty("emailPassphrase"))
		})
	}
	internal val TO = SETTINGS_PROPERTIES.getProperty("emailTo")

	/**
	 * Sends an email to [to] with the provided subject, body and attachments from the address specified under *emailFrom* in [SETTINGS_PROPERTIES].
	 *
	 * Email is sent in the [SESSION] created with values from [SETTINGS_PROPERTIES]
	 *
	 * @param to the email address to send the email to
	 * @param subject the subject of the email
	 * @param body the body text of the email
	 * @param attachments images to attach to the email, should be of type image/jpeg
	 */
	fun send(to: String, subject: String, body: String, vararg attachments: Pair<String, java.net.URL?>) {
		val message = javax.mail.internet.MimeMessage(SESSION)
		message.setFrom(javax.mail.internet.InternetAddress(FROM))
		message.setRecipients(javax.mail.Message.RecipientType.TO, javax.mail.internet.InternetAddress.parse(to))
		message.subject = subject
		@Suppress("UNCHECKED_CAST") val notNullAttachments = attachments.filter { it.second != null } as List<Pair<String, java.net.URL>>
		if (notNullAttachments.isEmpty()) {
			LOGGER.debug(DebugMarker.DEBUG1.marker, "No attachments")
			message.setText(body)
		} else {
			LOGGER.debug(DebugMarker.DEBUG1.marker, "Adding attachments")
			val multipart = javax.mail.internet.MimeMultipart()
			val contentBodyPart = javax.mail.internet.MimeBodyPart()
			contentBodyPart.setText(body)
			multipart.addBodyPart(contentBodyPart)
			notNullAttachments.forEach { (filename, attachmentUri) ->
				LOGGER.debug(DebugMarker.DEBUG3.marker, "Attaching file $filename ($attachmentUri)")
				val attachmentBodyPart = javax.mail.internet.MimeBodyPart()
				attachmentBodyPart.dataHandler = javax.activation.DataHandler(JpgUrlDataSource(attachmentUri))
				attachmentBodyPart.fileName = filename
				attachmentBodyPart.disposition = javax.mail.Part.ATTACHMENT
				multipart.addBodyPart(attachmentBodyPart)
				LOGGER.debug(DebugMarker.DEBUG2.marker, "Attached file $filename ($attachmentUri)")

			}
			message.setContent(multipart)
		}
		javax.mail.Transport.send(message)
		LOGGER.debug("Sent email with Message-ID: ${message.messageID}")
	}

	/**
	 * Data Source with ContentType "image/jpeg"
	 * @param url URL to the image
	 */
	class JpgUrlDataSource internal constructor(url: java.net.URL):javax.activation.URLDataSource(url) {

		override fun getContentType(): String = "image/jpeg"
	}
}
