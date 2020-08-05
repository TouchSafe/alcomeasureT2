### Files

#### Project Root

##### [settings.properties](settings.properties)

Config file for properties used in the program

#### au.com.touchsafe.alcomeasure
###### src/main/kotlin/au/com/touchsafe/alcomeasure

##### [AlcoMeasure.kt](src/main/kotlin/au/com/touchsafe/alcomeasure/AlcoMeasure.kt)

Contains the functions
  - displayMessage(message: String)
    - Displays a message on the AlcoMeasure device
  - performTest(user: User)
    -  Performs a test using the AlcoMeasure device
  - extractValue(body: String, tagStart: String)
    - Extracts a value from an XML body with the specified starting tag

##### [Email](src/main/kotlin/au/com/touchsafe/alcomeasure/Email.kt)

Contains the email sending function
  - send(to: String, subject: String, body: String, vararg attachments: Pair<String, java.net.URL?>)
    - sends an email to the specified email address, with the provided subject, body and attachments, which should be JPEGs

##### [Input](src/main/kotlin/au/com/touchsafe/alcomeasure/Input.kt)

Contains the function that reads input from the AlcoMeasure unit to get the scanned ID card

It currently reads input from all keyboards

##### [SqlServer](src/main/kotlin/au/com/touchsafe/alcomeasure/SqlServer.kt)

Contains the functions
  - validateId(connection: java.sql.Connection, id: Rfid)
    - Validates the Rfid with the database on the provided connection
  - storeResult(connection: java.sql.Connection, user: User, result: Result)
    - Stores the AlcoMeasure result for the user in the database
  - downloadAndStorePhoto(connection: java.sql.Connection, photoUri: java.net.URL)
    - Stores the photo in the database

##### [application.kt](src/main/kotlin/au/com/touchsafe/alcomeasure/application.kt)

Contains the main function, which runs a loop which:
  - reads the ID of the scanned card
  - checks the ID against the database
  - if the ID is unrecognised, it prints a message
  - else,
    - it performs a test
    - stores the result in the database
    - if the result was over, it sends an email

#### resources
###### src/main/resources

##### [logback.xml](src/main/resources/logback.xml)

logback.xml used by slf4j during testing, will be ignored by Gradle shadowJar build

##### [logback-deployment.xml](src/main/resources/logback-deployment.xml)

logback.xml used by slf4j in production, will be used as logback.xml by Gradle shadowJar build

##### [messages_en.properties](src/main/resources/messages_en.properties)

Contains messages and templates for emails and for messages sent to the AlcoMeasure machine

##### [settings.properties](src/main/resources/settings.properties)

This file doesn't appear to be used, unlike the file with the same name in the project root

#### sql
###### src/main/sql

##### [alcomeasure.sql](src/main/sql/alcomeasure.sql)

SQL for creating the AlcoMeasure result table

### Tests

#### au.com.touchsafe.alcomeasure
###### src/test/kotlin/au/com/touchsafe/alcomeasure

##### [EmailTest](src/test/kotlin/au/com/touchsafe/alcomeasure/EmailTest.kt)

Tests that the Email send function correctly sends an email over SMTP