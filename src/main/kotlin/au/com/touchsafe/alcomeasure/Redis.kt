package au.com.touchsafe.alcomeasure

import io.lettuce.core.RedisClient
import java.time.LocalDateTime

// internal const val EMAIL_COUNTDOWN_SECONDS = 60L      // this should eventually be 5 mins
internal const val EMAIL_COUNTDOWN_SECONDS = 300L      // this should eventually be 5 mins

object Redis {
    // On the ts-redis server, example of pulling info out using the redis-cli util
    // pull out all keys with pending tests. A key will get deleted once a test is done
    // 127.0.0.1:6379> keys alcomeasureT2:e022520a-c7ae-49d7-b202-e7933fb83dcb:cardscanned:user:*:alcoholtestpending
    // get the user id of a pending test
    // 127.0.0.1:6379> get alcomeasureT2:e022520a-c7ae-49d7-b202-e7933fb83dcb:cardscanned:user:3212:alcoholtestpending

    // TODO handle Redis connection and timeout errors. The internet can go down or not be available on application start.
    private val redisClient : RedisClient by lazy { RedisClient.create("redis://10.11.0.22:6379/0") }  // TODO server info for Redis should be coming from the configuration file
    private val connection by lazy { redisClient.connect() }
    private lateinit var applicationID : String

    init {
        LOGGER.debug("Redis().init")
        // redisClient.setDefaultTimeout(20, TimeUnit.SECONDS);
        // TODO handle applicationID when it's not set in properties file. Program should probably abort with a suitable message for the end-user.
        applicationID = SETTINGS_PROPERTIES.getProperty("applicationID")
    }

    fun appPrefix() : String {
        return "alcomeasureT2:$applicationID"
    }

    fun userPrefix(id : Int) : String {
        return ":user:$id"
    }

    fun pushMessage(message: String) {
        LOGGER.debug("Redis.pushMessage()")
        val syncCommands = connection.sync()
        syncCommands["key"] = "Hello, Redis!"
    }

    fun cardScanned(id : Int, firstname: String, surname: String) {
        println("Redis.cardScanned()")
        println("Redis.cardScanned: id=$id")
        println("Redis.cardScanned: firstname=$firstname")
        println("Redis.cardScanned: surname=$surname")

        LOGGER.debug("Redis.cardScanned()")
        LOGGER.debug("Redis.cardScanned: id=$id")
        LOGGER.debug("Redis.cardScanned: firstname=$firstname")
        LOGGER.debug("Redis.cardScanned: surname=$surname")
        val currentDateTime = LocalDateTime.now()
        val syncCommands = connection.sync()
        // syncCommands["key"] = "Hello, Redis!"
        syncCommands[appPrefix() + userPrefix(id)] = id.toString()
        syncCommands[appPrefix() + userPrefix(id) + ":firstname"] = firstname
        syncCommands[appPrefix() + userPrefix(id) + ":surname"] = surname
        syncCommands[appPrefix() + userPrefix(id) + ":signin"] = currentDateTime.toString()
        syncCommands[appPrefix() + userPrefix(id) + ":alcoholtestpending"] = id.toString()
        syncCommands[appPrefix() + userPrefix(id) + ":alcoholtestpendingexpire"] = id.toString()
        syncCommands.expire(appPrefix() + userPrefix(id) + ":alcoholtestpendingexpire", EMAIL_COUNTDOWN_SECONDS)
        syncCommands.del(appPrefix() + userPrefix(id) + ":alcoholtestdone")
    }

    fun alcoholTestDone(id : Int) {
        LOGGER.debug("Redis.alcoholTestDone()")
        val currentDateTime = LocalDateTime.now()
        val syncCommands = connection.sync()
        syncCommands.del(appPrefix() + ":user:$id:alcoholtestpending")
        syncCommands.del(appPrefix() + ":user:$id:alcoholtestpendingexpire")

        syncCommands[appPrefix() + ":user:$id:alcoholtestdone"] = currentDateTime.toString()
    }

    fun applicationStarted() {
        val currentDateTime = LocalDateTime.now()
        // default format is DateTimeFormatter.ISO_DATE_TIME
        LOGGER.debug("Redis.applicationStarted()")
        LOGGER.info("Redis: applicationID", applicationID)
        println("Redis: applicationID: " + applicationID)
        LOGGER.info("Redis: Email.emailHost", Email.emailHost)
        LOGGER.info("Redis: Email.emailPort", Email.emailPort)
        val syncCommands = connection.sync()
        syncCommands["alcomeasureT2:$applicationID"] = applicationID
        syncCommands["alcomeasureT2:$applicationID:applicationstarted:datetime"] = currentDateTime.toString()
        syncCommands["alcomeasureT2:$applicationID:email:emailHost"] = Email.emailHost
        syncCommands["alcomeasureT2:$applicationID:email:emailPort"] = Email.emailPort
        syncCommands["alcomeasureT2:$applicationID:email:emailTo"] = Email.emailTo

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
        LOGGER.debug("Redis.alcoTestPendingEmailCheck()")
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

    fun alcoGetTTL(id : Int) : Long {
        val value: Long = connection.sync().ttl(appPrefix() + ":user:$id:alcoholtestpendingexpire")
        // println("$id - $value")
        return value
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

}



//syncCommands["key"] = "Hello, Redis!"
//
//connection.close()
//redisClient.shutdown()
