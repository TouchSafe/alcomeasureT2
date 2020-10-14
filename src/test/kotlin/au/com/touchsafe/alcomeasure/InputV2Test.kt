package au.com.touchsafe.alcomeasure

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jnativehook.GlobalScreen
import org.jnativehook.keyboard.NativeKeyEvent
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test

fun typeNativeKeyEvent(keyCode: Int, keyChar: Char) {
    GlobalScreen.postNativeEvent(NativeKeyEvent(NativeKeyEvent.NATIVE_KEY_PRESSED, 0, 0, keyCode, keyChar))
    GlobalScreen.postNativeEvent(NativeKeyEvent(NativeKeyEvent.NATIVE_KEY_RELEASED, 0, 0, keyCode, keyChar))
}

fun nativeKeyEventKeyCode(c: Char): Int {
    when (c) {
        '1' -> {
            return NativeKeyEvent.VC_1
        }
        '2' -> {
            return NativeKeyEvent.VC_2
        }
        '3' -> {
            return NativeKeyEvent.VC_3
        }
        '4' -> {
            return NativeKeyEvent.VC_4
        }
        '5' -> {
            return NativeKeyEvent.VC_5
        }
        '6' -> {
            return NativeKeyEvent.VC_6
        }
        '7' -> {
            return NativeKeyEvent.VC_7
        }
        '8' -> {
            return NativeKeyEvent.VC_8
        }
        '9' -> {
            return NativeKeyEvent.VC_9
        }
        '0' -> {
            return NativeKeyEvent.VC_0
        }
    }
    throw Exception("unexpected value")
}

fun Rfid.typeNativeKeyEvents() {
    this.facilityCode.toString().forEach { character ->
        typeNativeKeyEvent(nativeKeyEventKeyCode(character), character)
    }
    typeNativeKeyEvent(NativeKeyEvent.VC_SEMICOLON, ';')
    this.cardNumber.toString().forEach { character ->
        typeNativeKeyEvent(nativeKeyEventKeyCode(character), character)
    }
    typeNativeKeyEvent(NativeKeyEvent.VC_ENTER, '\n')
}

class InputV2Test {
    companion object {
        @BeforeAll
        @JvmStatic
        fun beforeTests() {
            GlobalScreen.registerNativeHook()
            GlobalScreen.addNativeKeyListener(InputV2.KEYBOARD_HOOK)
        }

        @AfterAll
        @JvmStatic
        fun afterTests() {
            // This causes the build to fail
            /*try {
                GlobalScreen.removeNativeKeyListener(InputV2.KEYBOARD_HOOK)
                GlobalScreen.unregisterNativeHook()
            } catch (e: Exception) {
            }*/
        }
    }

    //TODO: This test types things when running, is there a way to avoid this?
    @Test
    fun testValidRfid() {
        val expectedRfid = Rfid(3, 1234)
        // Launch key presses as a coroutine
        GlobalScope.launch {
            // Wait a couple of seconds before sending the keys
            delay(2000L)
            expectedRfid.typeNativeKeyEvents()
        }

        val rfid = InputV2.getId()

        assertNotNull(rfid)
        assertEquals(expectedRfid.facilityCode, rfid!!.facilityCode)
        assertEquals(expectedRfid.cardNumber, rfid.cardNumber)
    }

    @Test
    fun testRfidDifferentFacilityCode() {
        val expectedRfid = Rfid(3, 1234)
        val wrongRfid = Rfid(2, 1234)
        // Launch key presses as a coroutine
        GlobalScope.launch {
            // Wait a couple of seconds before sending the keys
            delay(2000L)
            wrongRfid.typeNativeKeyEvents()
        }

        val rfid = InputV2.getId()

        assertNotNull(rfid)
        assertNotEquals(expectedRfid.facilityCode, rfid!!.facilityCode)
        assertEquals(expectedRfid.cardNumber, rfid.cardNumber)
    }

    @Test
    fun testRfidDifferentCardNo() {
        val expectedRfid = Rfid(3, 1234)
        val wrongRfid = Rfid(3, 5678)
        // Launch key presses as a coroutine
        GlobalScope.launch {
            // Wait a couple of seconds before sending the keys
            delay(2000L)
            wrongRfid.typeNativeKeyEvents()
        }

        val rfid = InputV2.getId()

        assertNotNull(rfid)
        assertEquals(expectedRfid.facilityCode, rfid!!.facilityCode)
        assertNotEquals(expectedRfid.cardNumber, rfid.cardNumber)
    }

    @Test
    fun testRfidDifferent() {
        val expectedRfid = Rfid(3, 1234)
        val wrongRfid = Rfid(2, 5678)
        // Launch key presses as a coroutine
        GlobalScope.launch {
            // Wait a couple of seconds before sending the keys
            delay(2000L)
            wrongRfid.typeNativeKeyEvents()
        }

        val rfid = InputV2.getId()

        assertNotNull(rfid)
        assertNotEquals(expectedRfid.facilityCode, rfid!!.facilityCode)
        assertNotEquals(expectedRfid.cardNumber, rfid.cardNumber)
    }

    @Test
    fun testRfidInvalid() {
        // Launch key presses as a coroutine
        GlobalScope.launch {
            // Wait a couple of seconds before sending the keys
            delay(2000L)
            typeNativeKeyEvent(NativeKeyEvent.VC_T, 't')
            typeNativeKeyEvent(NativeKeyEvent.VC_E, 'e')
            typeNativeKeyEvent(NativeKeyEvent.VC_S, 's')
            typeNativeKeyEvent(NativeKeyEvent.VC_T, 't')
            typeNativeKeyEvent(NativeKeyEvent.VC_ENTER, '\n')
        }

        val rfid = InputV2.getId()
        assertNull(rfid)
    }

    @Test
    fun testRfidInvalidFacilityCode() {
        // Launch key presses as a coroutine
        GlobalScope.launch {
            // Wait a couple of seconds before sending the keys
            delay(2000L)
            typeNativeKeyEvent(NativeKeyEvent.VC_T, 't')
            typeNativeKeyEvent(NativeKeyEvent.VC_SEMICOLON, ';')
            typeNativeKeyEvent(NativeKeyEvent.VC_1, '1')
            typeNativeKeyEvent(NativeKeyEvent.VC_2, '2')
            typeNativeKeyEvent(NativeKeyEvent.VC_3, '3')
            typeNativeKeyEvent(NativeKeyEvent.VC_4, '4')
            typeNativeKeyEvent(NativeKeyEvent.VC_ENTER, '\n')
        }

        val rfid = InputV2.getId()
        assertNull(rfid)
    }

    @Test
    fun testRfidInvalidNoFacilityCode() {
        // Launch key presses as a coroutine
        GlobalScope.launch {
            // Wait a couple of seconds before sending the keys
            delay(2000L)
            typeNativeKeyEvent(NativeKeyEvent.VC_SEMICOLON, ';')
            typeNativeKeyEvent(NativeKeyEvent.VC_1, '1')
            typeNativeKeyEvent(NativeKeyEvent.VC_2, '2')
            typeNativeKeyEvent(NativeKeyEvent.VC_3, '3')
            typeNativeKeyEvent(NativeKeyEvent.VC_4, '4')
            typeNativeKeyEvent(NativeKeyEvent.VC_ENTER, '\n')
        }

        val rfid = InputV2.getId()
        assertNull(rfid)
    }

    @Test
    fun testRfidInvalidNoCardNo() {
        // Launch key presses as a coroutine
        GlobalScope.launch {
            // Wait a couple of seconds before sending the keys
            delay(2000L)
            typeNativeKeyEvent(NativeKeyEvent.VC_3, '3')
            typeNativeKeyEvent(NativeKeyEvent.VC_SEMICOLON, ';')
            typeNativeKeyEvent(NativeKeyEvent.VC_ENTER, '\n')
        }

        val rfid = InputV2.getId()
        assertNull(rfid)
    }

    @Test
    fun testRfidInvalidNumbers() {
        // Launch key presses as a coroutine
        GlobalScope.launch {
            // Wait a couple of seconds before sending the keys
            delay(2000L)
            typeNativeKeyEvent(NativeKeyEvent.VC_1, '1')
            typeNativeKeyEvent(NativeKeyEvent.VC_2, '2')
            typeNativeKeyEvent(NativeKeyEvent.VC_3, '3')
            typeNativeKeyEvent(NativeKeyEvent.VC_4, '4')
            typeNativeKeyEvent(NativeKeyEvent.VC_ENTER, '\n')
        }

        val rfid = InputV2.getId()
        assertNull(rfid)
    }

    @Test
    fun testRfidInvalidNoNumbers() {
        // Launch key presses as a coroutine
        GlobalScope.launch {
            // Wait a couple of seconds before sending the keys
            delay(2000L)
            typeNativeKeyEvent(NativeKeyEvent.VC_SEMICOLON, ';')
            typeNativeKeyEvent(NativeKeyEvent.VC_ENTER, '\n')
        }

        val rfid = InputV2.getId()
        assertNull(rfid)
    }
}