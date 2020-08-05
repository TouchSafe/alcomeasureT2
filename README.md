### Files

#### au.com.touchsafe.alcomeasure

##### AlcoMeasure.kt

Contains the functions
  - displayMessage(message: String)
    - Displays a message on the AlcoMeasure device
  - performTest(user: User)
    -  Performs a test using the AlcoMeasure device
  - extractValue(body: String, tagStart: String)
    - Extracts a value from an XML body with the specified starting tag

##### Email

Contains the email sending function
  - send(to: String, subject: String, body: String, vararg attachments: Pair<String, java.net.URL?>)
    - sends an email to the specified email address, with the provided subject, body and attachments, which should be JPEGs

##### Input

Contains the function that reads input from the AlcoMeasure unit to get the scanned ID card

It currently reads input from all keyboards


##### application.kt

Contains the main function, which runs a loop which:
  - reads the ID of the scanned card
  - checks the ID against the database
  - if the ID is unrecognised, it prints a message
  - else,
    - it performs a test
    - stores the result in the database
    - if the result was over, it sends an email

