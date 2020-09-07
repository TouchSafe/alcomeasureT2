package au.com.touchsafe.alcomeasure

import java.util.*
import javax.print.attribute.IntegerSyntax

/**
 * Simple demo that uses java.util.Timer to schedule a task
 * to execute once 5 seconds have passed.
 */
class Reminder(seconds: Int) {
    var timer: Timer = Timer()
    var secs: Int

    internal inner class RemindTask : TimerTask() {
        override fun run() {
            println(secs)
            println("Time's up!")
            timer.cancel() //Terminate the timer thread
        }
    }

    companion object {
        @JvmStatic
        fun main(args: Array<String>) {
            Reminder(5)
            println("Task scheduled.")
        }
    }

    init {
        secs = seconds
        timer.schedule(RemindTask(), seconds * 1000.toLong())
    }
}