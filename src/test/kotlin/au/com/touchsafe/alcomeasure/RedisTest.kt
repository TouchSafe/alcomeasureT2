package au.com.touchsafe.alcomeasure
import java.util.*
import kotlin.concurrent.schedule

import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotEquals
import java.util.*

class RedisTest {
    companion object {
        // private val settings_properties = Properties()
    }

    @Test
    fun testRedis() {
        Redis.applicationStarted()
        Redis.pushMessage("hello there")
        Redis.cardScanned(id = 1234, firstname = "Greg", surname = "Duffy")
        Redis.cardScanned(id = 3212, firstname = "Aidan", surname = "Davies")
    }

    @Test
    fun testCard1234() {
        Redis.cardScanned(id = 1234, firstname = "Greg", surname = "Duffy")
    }

    @Test
    fun testCard2312() {
        Redis.cardScanned(id = 2312, firstname = "Ryan", surname = "Carrier")
    }

    @Test
    fun testCard3212() {
        Redis.cardScanned(id = 3212, firstname = "Aidan", surname = "Davies")
    }


    @Test
    fun testAlcoholTestTTL() {
        println(Redis.alcoGetTTL(1234))
        println(Redis.alcoGetTTL(2312) )
                println(Redis.alcoGetTTL(3212))
    }

    @Test
    fun testAlcoholTestDone1234() {
        Redis.alcoholTestDone(1234)
    }

    @Test
    fun testAlcoholTestDone3212() {
        Redis.alcoholTestDone(3212)
    }

    @Test
    fun testAlcoTestScanAll() {
        Redis.alcoTestScanAll()
    }

    @Test
    fun testAlcoTestPending() {
        Redis.alcoTestPending()
    }

    @Test
    fun testAlcoTestPendingEmailCheck() {
        Redis.alcoTestPendingEmailCheck()
    }


    @Test
    fun testAlcoTestDone() {
        Redis.alcoTestDone()
    }

}
