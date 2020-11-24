# AlcoMeasure Program T2 - Working against the TouchSafe2 database

Program that interfaces with the AlcoMeasure unit to perform tests and import them into the TouchSafe database.

There is also a companion application called alcomeasureT2emailer that currently handled sending out reminder emails if
a user signs in but doesn't blow an Alcohol test in a reasonable time.

### Coding Guidelines

All dependencies should be managed fully by Gradle. It should be possible to check the project out of GitHub and
build it immediately with Gragle. (gradle build). The build.gradle.kts should take care of everything. Note that the
Kotlin Gradle DSL is being used, this the kts extension on the build file. Refer to https://gradle.org if in any doubt
on how to use Gradle.

Use TODO statements liberally in the code. Most IDE's can pick these up automatically and grep is your friend. If in
any doubt, write a TODO if you think of something while writing code. These can be reviewed from time to time and a
full issue or feature request created in GitHub and / or Jira at a later time.

Given this is Kotlin application there should be documentation done that conforms to the KDoc conventions.
See: https://kotlinlang.org/docs/reference/kotlin-doc.html. Remember, there can never be too many comments in code.

Code Smell is bad. https://en.wikipedia.org/wiki/Code_smell


### Miscellaneous Files
These files are not covered by the documentation

#### Project Root

##### [settings.properties](settings.properties)

Config file for properties used in the program. Properties are as follows:

applicationUUID - Unique identifier for the application. Generate on a Linux system using uuidgen utility. This was added
for use by the Redis code to distinguish between instances of the application and the associated AlcoMeasure Units. At
the moment there is a NUC and AlcoMeasure device at TouchSafe in Brisbane and one at Jellinbah Mine in the Admin sign-in
area. Example: applicationID = e022520a-c7ae-49d7-b202-e7933fb83dcb

alcomeasureHost - IP address or hostname of the AlcoMeasure Testing Device. This will have to be changed at each client
site. If this field is empty, a dummy result of zero will be used. Example: alcomeasureHost = 192.168.0.164
    
alcomeasurePort - TCP Port number that the AlcoMeasure unit is listening on. This is configured on the AlcoMeasure unit
and I believe the default port is 26000. Example: alcomeasurePort = 26000

dbHost - IP address of hostname of the Database Server which is running as the TouchSafe2 primary SQL server. This at
the moment is a Microsoft SQL Server. Example: dbHost = 192.168.0.15

<< STILL TO DO THE FOLLOWING >>

dbPort = 1433
dbDatabase = prod-mackenzie
dbUsername = dev-touchsafe
dbPassphrase = iind

emailAuth = false
emailFrom = noreply@touchsafe.com.au
emailHost = 192.168.1.52
emailPassphrase = passphrase
emailPort = 1026
emailStartTls = false
emailTo = gduffy@byteback.com
emailUsername = username

emailLogLevel = FINEST
consoleLogLevel = INFO
fileLogLevel = INFO
mailAllReports = false

redisServer = 10.11.0.22
redisPort = 6379

#### resources
###### src/main/resources

##### [logback.xml](src/main/resources/logback.xml)

logback.xml used by slf4j during testing, will be ignored by Gradle shadowJar build

##### [logback-deployment.xml](src/main/resources/logback-deployment.xml)

logback.xml used by slf4j in production, will be used as logback.xml by Gradle shadowJar build

This logger config file can be used on a deployed system to bump up log levels to assist in debugging and
the like. The INFO log level should probably be the default on most systems.


##### [messages_en.properties](src/main/resources/messages_en.properties)

Contains messages and templates for emails and for messages sent to the AlcoMeasure machine

##### [settings.properties](src/main/resources/settings.properties)

This file doesn't appear to be used, unlike the file with the same name in the project root

#### sql
###### src/main/sql

##### [alcomeasure.sql](src/main/sql/alcomeasure.sql)

SQL for creating the AlcoMeasure result table. This would normally be run against an existing TouchSafe2 database. Not
sure if the intention was to use this with TouchSafe3.

### Building

From the command line:

    gradle shadowJar

From the Gradle plugin window in IntelliJ:

    alcomeasure/Tasks/shadow/shadowJar

Output will be in the build/distributions/ dir in the form of a single jar with the version number included in the
name. This Jar is all that needs to be copied to a live site. All jars and dependencies are bundled into the one jar
by the shadowJar plugin.

For example:

    alcomeasure-1.0.4-SNAPSHOT.jar


 