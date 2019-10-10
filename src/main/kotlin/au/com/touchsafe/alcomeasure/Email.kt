package au.com.touchsafe.alcomeasure

object Email {

	private val FROM = SETTINGS_BUNDLE.getString("emailFrom")
	private val SESSION = java.util.Properties().let { properties ->
		properties["mail.smtp.auth"] = SETTINGS_BUNDLE.getString("emailAuth")
		properties["mail.smtp.starttls.enable"] = SETTINGS_BUNDLE.getString("emailStartTls")
		properties["mail.smtp.host"] = SETTINGS_BUNDLE.getString("emailHost")
		properties["mail.smtp.port"] = SETTINGS_BUNDLE.getString("emailPort")
		javax.mail.Session.getInstance(properties, object : javax.mail.Authenticator() {
			override fun getPasswordAuthentication() = javax.mail.PasswordAuthentication(SETTINGS_BUNDLE.getString("emailUsername"), SETTINGS_BUNDLE.getString("emailPassphrase"))
		})
	}
	internal val TO = SETTINGS_BUNDLE.getString("emailTo")

	fun send(to: String, subject: String, body: String, vararg attachments: Pair<String, java.net.URL?>) {
		val message = javax.mail.internet.MimeMessage(SESSION)
		message.setFrom(javax.mail.internet.InternetAddress(FROM))
		message.setRecipients(javax.mail.Message.RecipientType.TO, javax.mail.internet.InternetAddress.parse(to))
		message.subject = subject
		val notNullAttachments = attachments.filter { it.second != null }
		if (notNullAttachments.isEmpty()) {
			message.setText(body)
		} else {
			val multipart = javax.mail.internet.MimeMultipart()
			val contentBodyPart = javax.mail.internet.MimeBodyPart()
			contentBodyPart.setText(body)
			multipart.addBodyPart(contentBodyPart)
			notNullAttachments.forEach { (filename, attachmentUri) ->
				val attachmentBodyPart = javax.mail.internet.MimeBodyPart()
				attachmentBodyPart.dataHandler = javax.activation.DataHandler(attachmentUri)
				attachmentBodyPart.fileName = filename
				multipart.addBodyPart(attachmentBodyPart)
			}
			message.setContent(multipart)
		}
		javax.mail.Transport.send(message)
	}
}
