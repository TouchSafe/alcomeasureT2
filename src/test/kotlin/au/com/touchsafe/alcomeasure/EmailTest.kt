package au.com.touchsafe.alcomeasure

import com.icegreen.greenmail.util.GreenMail
import com.icegreen.greenmail.util.ServerSetup
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test


class EmailTest {

    @Test
    fun testEmail() {
        // This will enable FINEST logging for com.sun.mail, showing detailed steps of the email sending process
        /*val logger = Logger.getLogger("com.sun.mail")
        logger.level = Level.FINEST
        val handler = ConsoleHandler()
        handler.formatter = SimpleFormatter()
        handler.level = Level.FINEST
        logger.addHandler(handler)*/


        // Default GreenMail SMTP uses :25
        val testSmtpSetup = ServerSetup(3025, null, ServerSetup.PROTOCOL_SMTP)
        val greenMail = GreenMail(testSmtpSetup)
        greenMail.start()

        val smtpSession = greenMail.smtp.createSession()

        // Clear SETTINGS_PROPERTIES and add test values
        SETTINGS_PROPERTIES.clear()
        // Add values from GreenMail server
        // Set default to false for first two, as will likely be null
        SETTINGS_PROPERTIES.setProperty("emailAuth", smtpSession.getProperty("mail.smtp.auth") ?: "false")
        SETTINGS_PROPERTIES.setProperty("emailStartTls", smtpSession.getProperty("mail.smtp.starttls.enable") ?: "false")
        SETTINGS_PROPERTIES.setProperty("emailHost", smtpSession.getProperty("mail.smtp.host"))
        SETTINGS_PROPERTIES.setProperty("emailPort", smtpSession.getProperty("mail.smtp.port"))
        // Add test values
        SETTINGS_PROPERTIES.setProperty("emailFrom", "test@alcomeasure")
        SETTINGS_PROPERTIES.setProperty("emailPassphrase", "passphrase")
        SETTINGS_PROPERTIES.setProperty("emailTo", "root@mailserver")
        SETTINGS_PROPERTIES.setProperty("emailUsername", "username")

        val emailSubject = "Test Email"
        val emailBody = "Test email body."

        Email.send(SETTINGS_PROPERTIES.getProperty("emailTo"), emailSubject, emailBody)

        assertTrue(greenMail.waitForIncomingEmail(1))
        val messages = greenMail.receivedMessages
        assertEquals(1, messages.size)
        assertEquals(emailSubject, messages[0].subject)
        // Have to trim \r\n from end of email body
        val actualEmailBody = messages[0].content.toString().trimEnd('\r', '\n')
        assertEquals(emailBody, actualEmailBody)

        greenMail.stop()
    }
}
