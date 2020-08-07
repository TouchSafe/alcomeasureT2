# AlcoMeasure Program

Program that interfaces with the AlcoMeasure unit to perform tests and import them into the TouchSafe database


### Miscellaneous Files
These files are not covered by the documentation

#### Project Root

##### [settings.properties](settings.properties)

Config file for properties used in the program

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
