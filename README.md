# Continuous Integration (CI) Assignment
##### The assignment done by group 8 for the course DD2480 Software Engineering Fundamentals.
This project is about implementing a CI (Continuous Integration) server by subscribing to GitHub's built-in webhooks. The CI server should send notifications whenever a push happens and compile and run all tests on the branch where the push happened.

## Files structure
```bash
├── README.md
├── pom.xml
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

## Tutorial

#### Setup Github for Webhook
In order to use this CI with your github project you need to link github to the computer you run the software on.
* Go to the github repository you want to link to the CI and then go to `Settings >> Webhooks`, click on `Add webhook`.
* In `Payload URL` enter the forwarding URL (eg `http://8929b010.ngrok.io`) and click on `Add webhook`. 
* Generate a token at github and put it in a file called `.token` and add it to your repository.

#### Run and build the program
To run and build the program, enter ` ` in the terminal.

#### Test Cases
The program also has test cases for the `features` class.
The program has test cases for CI futures which are in the `features` class.
These test cases are used on `simpleprojects` to test the server's `testing` and `compilation`.


## Contributions
|  Name | Contribution | Issues |
|:-------|:--------|:--------|
|Oliver Lindblad| | |
|Alexander Binett | Implemenetig tesdting and features class| |
|Rabi Hanna	| | |
|Hasti Mohebali Zadeh| implmented notifications class and README| |
| | | |

Pull requests were regularly done and were reviewed before merging with the remote main.

#### Commit Prefixes
`feat`
`fix`
`doc`
