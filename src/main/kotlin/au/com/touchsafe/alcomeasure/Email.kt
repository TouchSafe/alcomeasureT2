package au.com.touchsafe.alcomeasure

object Email {

	private val FROM = SETTINGS_PROPERTIES.getProperty("emailFrom")
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

	fun send(to: String, subject: String, body: String, vararg attachments: Pair<String, java.net.URL?>) {
		val message = javax.mail.internet.MimeMessage(SESSION)
		message.setFrom(javax.mail.internet.InternetAddress(FROM))
		message.setRecipients(javax.mail.Message.RecipientType.TO, javax.mail.internet.InternetAddress.parse(to))
		message.subject = subject
		@Suppress("UNCHECKED_CAST") val notNullAttachments = attachments.filter { it.second != null } as List<Pair<String, java.net.URL>>
		if (notNullAttachments.isEmpty()) {
			message.setText(body)
		} else {
			val multipart = javax.mail.internet.MimeMultipart()
			val contentBodyPart = javax.mail.internet.MimeBodyPart()
			contentBodyPart.setText(body)
			multipart.addBodyPart(contentBodyPart)
			notNullAttachments.forEach { (filename, attachmentUri) ->
				val attachmentBodyPart = javax.mail.internet.MimeBodyPart()
				attachmentBodyPart.dataHandler = javax.activation.DataHandler(JpgUrlDataSource(attachmentUri))
				attachmentBodyPart.fileName = filename
				attachmentBodyPart.disposition = javax.mail.Part.ATTACHMENT
				multipart.addBodyPart(attachmentBodyPart)
			}
			message.setContent(multipart)
		}
		javax.mail.Transport.send(message)
		LOGGER.debug("Sent email \"${subject}\" to ${Email.TO}")
	}

	class JpgUrlDataSource internal constructor(url: java.net.URL):javax.activation.URLDataSource(url) {

		override fun getContentType(): String = "image/jpeg"
	}
}
