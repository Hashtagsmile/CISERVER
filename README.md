# Continuous Integration (CI) Assignment
##### The assignment done by group 8 for the course DD2480 Software Engineering Fundamentals.
This project is about implementing a CI (Continuous Integration) server by subscribing to GitHub's built-in webhooks. The CI server should send notifications whenever a push happens and compile and run all tests on the branch where the push happened. The CI server supports executing the automated tests.

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
    │       ├── Features.java
    │       └── Notifications.java
    └── test
        └── java
            └── Testing.java
```
`ContinousIntegrationServer` include the main method.

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
The program also has test cases for the `features` class.
The program has test cases for CI futures which are in the `features` class.
These test cases are used on `simpleprojects` to test the server's `testing` and `compilation`.

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
