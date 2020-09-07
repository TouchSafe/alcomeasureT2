package au.com.touchsafe.alcomeasure

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.util.*

class RedisSettingsTest {
    /**
     * Checks the expected values are in the application's settings.properties file, This test should be greatly
     * expanded to look for things like default values if the setting file doesn't contain a value. Would also like
     * to see a way to change a setting in this file and persist it back to the file. I'm pretty sure that programmatic
     * settings can't be done as yet in AlcoMeasure. All settings appear to the manually done (DANGEROUS!).
     */

    companion object {
        private val settings_properties = Properties()
        internal val SETTINGS_PROPERTIES: java.util.Properties = java.util.Properties().apply { java.io.FileInputStream("./settings.properties").use { load(it) } }

        // private val FROM = SETTINGS_PROPERTIES.getProperty("emailFrom")

        @BeforeAll
        @JvmStatic
        fun beforeTests() {
//            // Clear SETTINGS_PROPERTIES
//            settings_properties.clear()
//            SETTINGS_PROPERTIES.stringPropertyNames().forEach { propertyName ->
//                settings_properties.setProperty(propertyName, SETTINGS_PROPERTIES.getProperty(propertyName))
//            }
//            SETTINGS_PROPERTIES.clear()
        }

        @BeforeEach
        fun beforeEachTest() {
            println("beforeEach")
//            SETTINGS_PROPERTIES.clear()
//
//            // Add values for Redis server
//            // Set default to false for first two, as will likely be null
//            SETTINGS_PROPERTIES.setProperty("emailAuth", smtpSession.getProperty("mail.smtp.auth") ?: "false")
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
    fun testNotEqualsAssert() {
        // @BeforeEach isn't running
        beforeEachTest()

        assertNotEquals(1, 2)

        // @AfterEach isn't running
        afterEachTest()
    }


    @Test
    fun testRedisPort() {
        // @BeforeEach isn't running
        beforeEachTest()

        assertEquals(settings_properties.getProperty("redisPort"), "6379")

        // @AfterEach isn't running
        afterEachTest()
    }

    @Test
    fun testRedisServer() {
        // @BeforeEach isn't running
        beforeEachTest()

        assertEquals(settings_properties.getProperty("redisServer"), "10.11.0.22")

        // @AfterEach isn't running
        afterEachTest()
    }

    @Test
    fun testApplicationID() {
        // @BeforeEach isn't running
        beforeEachTest()
        println(SETTINGS_PROPERTIES.getProperty("applicationID"))
        assertEquals(SETTINGS_PROPERTIES.getProperty("applicationID"), "e022520a-c7ae-49d7-b202-e7933fb83dcb")
        //assertNotNull(settings_properties.getProperty("applicationID"))


        // @AfterEach isn't running
        afterEachTest()
    }


}
