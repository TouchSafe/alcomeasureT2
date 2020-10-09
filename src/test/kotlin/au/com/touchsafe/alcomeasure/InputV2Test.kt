package au.com.touchsafe.alcomeasure

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
        // rawCode is not relevant for this test, and can change between OSes
        GlobalScreen.postNativeEvent(NativeKeyEvent(NativeKeyEvent.NATIVE_KEY_TYPED, 0, 0, NativeKeyEvent.VC_3, '3'))
        GlobalScreen.postNativeEvent(NativeKeyEvent(NativeKeyEvent.NATIVE_KEY_TYPED, 0, 0, NativeKeyEvent.VC_SEMICOLON, ';'))
        GlobalScreen.postNativeEvent(NativeKeyEvent(NativeKeyEvent.NATIVE_KEY_TYPED, 0, 0, NativeKeyEvent.VC_1, '1'))
        GlobalScreen.postNativeEvent(NativeKeyEvent(NativeKeyEvent.NATIVE_KEY_TYPED, 0, 0, NativeKeyEvent.VC_2, '2'))
        GlobalScreen.postNativeEvent(NativeKeyEvent(NativeKeyEvent.NATIVE_KEY_TYPED, 0, 0, NativeKeyEvent.VC_3, '3'))
        GlobalScreen.postNativeEvent(NativeKeyEvent(NativeKeyEvent.NATIVE_KEY_TYPED, 0, 0, NativeKeyEvent.VC_4, '4'))

        val rfid = InputV2.getId()

        assertEquals(expectedRfid.facilityCode, rfid.facilityCode)
        assertEquals(expectedRfid.cardNumber, rfid.cardNumber)
    }
}