package au.com.touchsafe.alcomeasure

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jnativehook.GlobalScreen
import org.jnativehook.keyboard.NativeKeyEvent
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.assertEquals
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

        assertEquals(expectedRfid.facilityCode, rfid.facilityCode)
        assertEquals(expectedRfid.cardNumber, rfid.cardNumber)
    }
}