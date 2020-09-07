package au.com.touchsafe.alcomeasure

import io.lettuce.core.RedisClient
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.*

class RedisConnectionTest {

    companion object {
//        lateinit var greenMail: GreenMail
//        private val settings_properties = Properties()

//        private lateinit var smtpSession: Session

        @BeforeAll
        @JvmStatic
        fun beforeTests() {
            // Clear SETTINGS_PROPERTIES
//            settings_properties.clear()
//            SETTINGS_PROPERTIES.stringPropertyNames().forEach { propertyName ->
//                settings_properties.setProperty(propertyName, SETTINGS_PROPERTIES.getProperty(propertyName))
//            }
//            SETTINGS_PROPERTIES.clear()

//            val testSmtpSetup = ServerSetup(3025, null, ServerSetup.PROTOCOL_SMTP)
//            greenMail = GreenMail(testSmtpSetup)
//            greenMail.start()
//
//            smtpSession = greenMail.smtp.createSession()
        }

        @BeforeEach
        fun beforeEachTest() {
//            SETTINGS_PROPERTIES.clear()
//
//            // Add values from GreenMail server
//            // Set default to false for first two, as will likely be null
//            SETTINGS_PROPERTIES.setProperty("emailAuth", smtpSession.getProperty("mail.smtp.auth") ?: "false")
//            SETTINGS_PROPERTIES.setProperty("emailStartTls", smtpSession.getProperty("mail.smtp.starttls.enable")
//                    ?: "false")
//            SETTINGS_PROPERTIES.setProperty("emailHost", smtpSession.getProperty("mail.smtp.host"))
//            SETTINGS_PROPERTIES.setProperty("emailPort", smtpSession.getProperty("mail.smtp.port"))
//
//            // Add test values
//            SETTINGS_PROPERTIES.setProperty("emailFrom", "test@alcomeasure")
//            SETTINGS_PROPERTIES.setProperty("emailPassphrase", "passphrase")
//            SETTINGS_PROPERTIES.setProperty("emailTo", "root@mailserver")
//            SETTINGS_PROPERTIES.setProperty("emailUsername", "username")
        }

        @AfterEach
        fun afterEachTest() {
//            greenMail.purgeEmailFromAllMailboxes()
        }

        @AfterAll
        @JvmStatic
        fun afterTests() {
//            SETTINGS_PROPERTIES.clear()
//            settings_properties.stringPropertyNames().forEach { propertyName ->
//                SETTINGS_PROPERTIES.setProperty(propertyName, settings_properties.getProperty(propertyName))
//            }
//
//            // Stop GreenMail
//            greenMail.stop()
        }
    }

    @Test
    fun testConnection() {
        // @BeforeEach isn't running
        beforeEachTest()

//        val emailSubject = "Test Email"
//        val emailBody = "Test email body."
//
//        Email.send(SETTINGS_PROPERTIES.getProperty("emailTo"), emailSubject, emailBody)
//
//        assertTrue(greenMail.waitForIncomingEmail(1))
//        val messages = greenMail.receivedMessages
//        assertEquals(1, messages.size)
//        assertEquals(emailSubject, messages[0].subject)
//        // Have to trim \r\n from end of email body
//        val actualEmailBody = messages[0].content.toString().trimEnd('\r', '\n')
//        assertEquals(emailBody, actualEmailBody)

        // val redisClient = RedisClient.create("redis://password@localhost:6379/0")
        val redisClient = RedisClient.create("redis://10.11.0.22:6379/0")
        val connection = redisClient.connect()
        val syncCommands = connection.sync()

        syncCommands["key"] = "Hello, Redis!"

        connection.close()
        redisClient.shutdown()

                // assertEquals(1, 2)



        // @AfterEach isn't running
        afterEachTest()

    }
}