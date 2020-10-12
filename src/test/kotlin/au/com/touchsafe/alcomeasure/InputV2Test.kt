package au.com.touchsafe.alcomeasure

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jnativehook.GlobalScreen
import org.jnativehook.keyboard.NativeKeyEvent
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class InputV2Test {
    //TODO: This test currently doesn't work
    @Test
    fun testValidRfid() {
        GlobalScreen.registerNativeHook()
        GlobalScreen.addNativeKeyListener(InputV2.KEYBOARD_HOOK)
        val expectedRfid = Rfid(3, 1234)
        // Launch key presses as a coroutine
        GlobalScope.launch {
            // Wait a couple of seconds before sending the keys
            delay(2000L)
            GlobalScreen.postNativeEvent(NativeKeyEvent(NativeKeyEvent.NATIVE_KEY_PRESSED, 0, 0, NativeKeyEvent.VC_UNDEFINED, '3'))
            GlobalScreen.postNativeEvent(NativeKeyEvent(NativeKeyEvent.NATIVE_KEY_RELEASED, 0, 0, NativeKeyEvent.VC_3, '3'))
            GlobalScreen.postNativeEvent(NativeKeyEvent(NativeKeyEvent.NATIVE_KEY_PRESSED, 0, 0, NativeKeyEvent.VC_SEMICOLON, ';'))
            GlobalScreen.postNativeEvent(NativeKeyEvent(NativeKeyEvent.NATIVE_KEY_RELEASED, 0, 0, NativeKeyEvent.VC_SEMICOLON, ';'))
            GlobalScreen.postNativeEvent(NativeKeyEvent(NativeKeyEvent.NATIVE_KEY_PRESSED, 0, 0, NativeKeyEvent.VC_1, '1'))
            GlobalScreen.postNativeEvent(NativeKeyEvent(NativeKeyEvent.NATIVE_KEY_RELEASED, 0, 0, NativeKeyEvent.VC_1, '1'))
            GlobalScreen.postNativeEvent(NativeKeyEvent(NativeKeyEvent.NATIVE_KEY_PRESSED, 0, 0, NativeKeyEvent.VC_2, '2'))
            GlobalScreen.postNativeEvent(NativeKeyEvent(NativeKeyEvent.NATIVE_KEY_RELEASED, 0, 0, NativeKeyEvent.VC_2, '2'))
            GlobalScreen.postNativeEvent(NativeKeyEvent(NativeKeyEvent.NATIVE_KEY_PRESSED, 0, 0, NativeKeyEvent.VC_3, '3'))
            GlobalScreen.postNativeEvent(NativeKeyEvent(NativeKeyEvent.NATIVE_KEY_RELEASED, 0, 0, NativeKeyEvent.VC_3, '3'))
            GlobalScreen.postNativeEvent(NativeKeyEvent(NativeKeyEvent.NATIVE_KEY_PRESSED, 0, 0, NativeKeyEvent.VC_4, '4'))
            GlobalScreen.postNativeEvent(NativeKeyEvent(NativeKeyEvent.NATIVE_KEY_RELEASED, 0, 0, NativeKeyEvent.VC_4, '4'))
            //TODO: This doesn't work. Need to either get NATIVE_KEY_TYPED events working, or find a way to do it with NATIVE_KEY_PRESSED - maybe if there's a KeyCode
            GlobalScreen.postNativeEvent(NativeKeyEvent(NativeKeyEvent.NATIVE_KEY_TYPED, 0, 0, NativeKeyEvent.VC_UNDEFINED, '\r'))
        }

        val rfid = InputV2.getId()

        assertEquals(expectedRfid.facilityCode, rfid.facilityCode)
        assertEquals(expectedRfid.cardNumber, rfid.cardNumber)
    }
}