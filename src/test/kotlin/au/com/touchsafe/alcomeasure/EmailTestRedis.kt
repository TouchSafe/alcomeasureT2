package au.com.touchsafe.alcomeasure

import au.com.touchsafe.alcomeasure.util.logging.DebugMarker
import com.icegreen.greenmail.util.GreenMail
import com.icegreen.greenmail.util.ServerSetup
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.*
import java.util.*
import javax.mail.Session


class EmailTestRedis {

    companion object {
        lateinit var greenMail: GreenMail
        private val settings_properties = Properties()

        private lateinit var smtpSession: Session

        @BeforeAll
        @JvmStatic
        fun beforeTests() {
        }

        @BeforeEach
        fun beforeEachTest() {
        }

        @AfterEach
        fun afterEachTest() {
        }

        @AfterAll
        @JvmStatic
        fun afterTests() {
            }
        }


    @Test
    fun testSend() {
        // @BeforeEach isn't running
        // beforeEachTest()

        val firstname = "Joe"
        val surname = "Blogs"
        val value = "test value"
        val body = "email body"
        

        val subject = "5 Minute Warning Email"
        val emailto = "gduffy@byteback.com"

        val emailBody = java.text.MessageFormat.format(body, firstname, surname, value)
        Email.send(emailto, subject, emailBody)

        // @AfterEach isn't running
        // afterEachTest()

    }
}

