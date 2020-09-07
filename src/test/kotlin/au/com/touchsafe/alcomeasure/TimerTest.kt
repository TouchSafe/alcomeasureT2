package au.com.touchsafe.alcomeasure
import java.util.*
import kotlin.concurrent.schedule

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import java.util.*

class TimerTest {
    companion object {
        // private val settings_properties = Properties()

    }

    @Test
    fun testTimerFiveSeconds() {
        Reminder(2)
        Reminder(5)
        Thread.sleep(8000L) // block main thread for 8 seconds to keep JVM alive
    }
}
