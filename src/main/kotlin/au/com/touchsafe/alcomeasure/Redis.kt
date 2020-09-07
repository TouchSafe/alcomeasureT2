package au.com.touchsafe.alcomeasure

import io.lettuce.core.RedisClient
import io.lettuce.core.ScanArgs
import io.lettuce.core.ScanIterator
import java.time.LocalDateTime

internal const val EMAIL_COUNTDOWN_SECONDS = 60L

object Redis {
    // pull out all keys with pending tests. A key will get deleted once a test is done
    // 127.0.0.1:6379> keys alcomeasureT2:e022520a-c7ae-49d7-b202-e7933fb83dcb:cardscanned:user:*:alcoholtestpending
    // get the user id of a pending test
    // 127.0.0.1:6379> get alcomeasureT2:e022520a-c7ae-49d7-b202-e7933fb83dcb:cardscanned:user:3212:alcoholtestpending

    // TODO handle Redis connection and timeout errors. The internet can go down or not be available on application start.
    private val redisClient : RedisClient by lazy { RedisClient.create("redis://10.11.0.22:6379/0") }
    private val connection by lazy { redisClient.connect() }
    private lateinit var applicationID : String

    init {
        println("Redis init")
        // redisClient.setDefaultTimeout(20, TimeUnit.SECONDS);
        // TODO handle applicationID when it's not set in properties file
        applicationID = SETTINGS_PROPERTIES.getProperty("applicationID")
        println(applicationID)
    }

    fun pushMessage(message: String) {
        // LOGGER.info("Display message:$message:")
        val syncCommands = connection.sync()
        syncCommands["key"] = "Hello, Redis!"
    }

    fun cardScanned(id : Int, firstname: String, surname: String) {
        val currentDateTime = LocalDateTime.now()
        val syncCommands = connection.sync()
        // syncCommands["key"] = "Hello, Redis!"
        syncCommands["alcomeasureT2:$applicationID:cardscanned:user:id"] = id.toString()
        syncCommands["alcomeasureT2:$applicationID:cardscanned:user:$id:firstname"] = firstname
        syncCommands["alcomeasureT2:$applicationID:cardscanned:user:$id:surname"] = surname
        syncCommands["alcomeasureT2:$applicationID:cardscanned:user:$id:signin"] = currentDateTime.toString()
        syncCommands["alcomeasureT2:$applicationID:cardscanned:user:$id:alcoholtestpending"] = id.toString()
        syncCommands["alcomeasureT2:$applicationID:cardscanned:user:$id:alcoholtestpendingexpire"] = id.toString()
        syncCommands.expire("alcomeasureT2:$applicationID:cardscanned:user:$id:alcoholtestpendingexpire", EMAIL_COUNTDOWN_SECONDS)
        syncCommands.del("alcomeasureT2:$applicationID:cardscanned:user:$id:alcoholtestdone")
    }

    fun alcoholTestDone(id : Int) {
        val currentDateTime = LocalDateTime.now()
        val syncCommands = connection.sync()
        syncCommands.del("alcomeasureT2:$applicationID:cardscanned:user:$id:alcoholtestpending")
        syncCommands.del("alcomeasureT2:$applicationID:cardscanned:user:$id:alcoholtestpendingexpire")

        syncCommands["alcomeasureT2:$applicationID:cardscanned:user:$id:alcoholtestdone"] = currentDateTime.toString()
    }

    fun applicationStarted() {
        val currentDateTime = LocalDateTime.now()
        // default format is DateTimeFormatter.ISO_DATE_TIME
        val syncCommands = connection.sync()
        syncCommands["alcomeasureT2:$applicationID"] = applicationID
        syncCommands["alcomeasureT2:$applicationID:applicationstarted:datetime"] = currentDateTime.toString()
    }

    fun alcoholTestsPending() {
        // val scan: ScanIterator<String> = ScanIterator.scan(connection, ScanArgs.Builder.limit(50).match("key-foo"))
//        val scan: ScanIterator<String> = ScanIterator.scan(redisClient, ScanArgs.Builder.limit(50).match("key-foo"))
//
//
//        while (scan.hasNext()) {
//            val next = scan.next()
//        }
    }

    fun alcoTestScanAll() {
        val keys: List<String> = connection.sync().keys("*")
        var index = 1
        for (key in keys) {
            val value: String = connection.sync().get(key)
            println("$index. $key - $value")
            index++
        }
    }

    fun alcoTestPending() {
        // 127.0.0.1:6379> get alcomeasureT2:e022520a-c7ae-49d7-b202-e7933fb83dcb:cardscanned:user:3212:alcoholtestpending
        val keys: List<String> = connection.sync().keys("*alcoholtestpending*")
        var index = 1
        for (key in keys) {
            val value: String = connection.sync().get(key)
            println("$index. $key - $value")
            index++
        }
    }

    fun alcoTestPendingEmailCheck() {
        val keys: List<String> = connection.sync().keys("*alcoholtestpending")
        var index = 1
        for (key in keys) {
            val value: String = connection.sync().get(key)
            val emailRun: Long = alcoGetTTL(value.toInt())
            if (emailRun.equals(-2L)) {
                println("$index. $key - $value - ttl: $emailRun")
            }
            index++
        }
    }

    fun alcoTestDone() {
        val keys: List<String> = connection.sync().keys("*alcoholtestdone*")
        var index = 1
        for (key in keys) {
            val value: String = connection.sync().get(key)
            println("$index. $key - $value")
            index++
        }
    }

    fun alcoGetTTL(id : Int) : Long {
        val value: Long = connection.sync().ttl("alcomeasureT2:$applicationID:cardscanned:user:$id:alcoholtestpendingexpire")
        // println("$id - $value")
        return value
    }


}



//syncCommands["key"] = "Hello, Redis!"
//
//connection.close()
//redisClient.shutdown()
