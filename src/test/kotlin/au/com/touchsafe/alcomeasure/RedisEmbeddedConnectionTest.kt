package au.com.touchsafe.alcomeasure

import io.lettuce.core.RedisClient
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import redis.embedded.RedisServer

class RedisEmbeddedConnectionTest {

    companion object {
        lateinit var redisServer: RedisServer

        @BeforeAll
        @JvmStatic
        fun beforeTests() {
            redisServer = RedisServer(6379)
            redisServer.start()
        }

        @BeforeEach
        fun beforeEachTest() {
        }

        @AfterAll
        @JvmStatic
        fun afterTests() {
            redisServer.stop();
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
        val redisClient = RedisClient.create("redis://127.0.0.1:6379/0")
        val connection = redisClient.connect()
        val syncCommands = connection.sync()

        syncCommands["key"] = "Hello, Redis!"

        connection.close()
        redisClient.shutdown()

        // @AfterEach isn't running
        // afterEachTest()

    }
}