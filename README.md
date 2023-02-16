# Continuous Integration (CI) Assignment
##### The assignment done by group 8 for the course DD2480 Software Engineering Fundamentals.
This project is about implementing a CI (Continuous Integration) server by subscribing to GitHub's built-in webhooks. The CI server should compile and run all tests on the branch where the push happened, and then send a summary as a notification to chosen email addresses. The CI server supports executing the automated tests.

## Files structure
```bash
├── README.md
├── pom.xml
├── Team_Evaluation.pdf
├── simpleprojects
└── src
    ├── main
    │   └── java
    │       ├── ContinousIntegrationServer.java
    │       └── Features.java
    │      
    └── test
        └── java
            └── Testing.java
```
The CI server assumes that the downloaded repo is a Maven project.

`ContinousIntegrationServer` include the main method.

`Features` include the three features that should be implemented, compilation, testing, and notifications. The compilation feature was implemented by calling the 'mvn compile' command on the project downloaded. If the process gives an output that contain 'BUILD SUCCESS', the compilation has succeeded.
The testing feature was implemented in the same way, except that the 'mvn test' command was called. The notification feature was implemented by utilizing the javax.mail package to set up a session and then create a message. The message was sent through the 'Transport' class from the javax.mail package.

The test folder contains the unit-tests for this project.

The `simpleprojects` directory contains several small projects that are used for testing the CI server.

The `Team_Evaluation.pdf` is the document containing the team evaluation based on the Essence standard.

## Tutorial

#### Required software
* latest version of IntelliJ IDEA

#### Setup Github for Webhook
In order to use this CI with your github project you need to link github to the computer you run the software on.
* Go to the github repository you want to link to the CI and then go to `Settings >> Webhooks`, click on `Add webhook`.
* In `Payload URL` enter the forwarding URL (eg `http://8929b010.ngrok.io`) and click on `Add webhook`. 

#### Run and build the program
To run and build the program, 
* Open your project on IntelliJ IDEA
* Click on the `Build Project` (hammer) icon
* Open IDEA terminal and navigate to `src/main/java` directory
* Enter `ngrok http 8080`

## Test Cases
The program has test cases for the `Features` class.
The `Features` class contains the compilation feature, the testing feature, and the notification feature.
These test cases are used on `simpleprojects` to test the server's `testing` and `compilation`. The notification feature has no test cases since the
javax.mail package and the email providers does not provide a way to check if a sent email has been delivered. Therefore, notification was tested by manually checking that the email sent was received at the given email addresses. 

## Contributions
|  Name | Contribution |
|:-------|:--------|
|Oliver Lindblad| Implemented handle json, clone, install and compile repository functioncs|
|Alexander Binett |Implemented testing and features class|
|Rabi Hanna	| Implemented handle json, clone, install and compile repository functioncs and email notification|
|Hasti Mohebali Zadeh|Implmented notifications function and README file|

Team members worked on several assignments together as well through pair-programming.

Pull requests were regularly done and were reviewed before merging with the remote main.

#### Commit Prefixes
`feat`
`fix`
`doc`
