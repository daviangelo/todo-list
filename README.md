# TODO Service

The service provides an API to manage a simple to-do list.

### API Documentation

Since the service is running, the Swagger documentation is available at http://localhost:8080/swagger-ui/index.html# 

### TODO Item

The main idea is to manage TODO items with the following properties:

- description
- status (not done, done and past due)
- creation date
- due date
- done date

#### TODO Item creation
To create a new item it is mandatory to add a description and a due date. If the due date is equal or earlier than the current date, an addition of a new item will be not allowed. When the item is added the creation date is filled in with the current date and the status will be "not done".

#### Automatic update of TODO Item status to past due
A scheduled task is responsible to update the items with "not done" status to "past due" on the database when the current date is equal or greater than the due date. This task is parametrized in the configuration, and it is default set to run every hour.

#### TODO Item description update
An item is retrieved from the database based on the supplied id. A description update is allowed since item status is not "past due". If the item status is "not done" and the current date is equal or greater than the due date, the item will be saved on the database as "past due" and the description update will be not allowed as well.

#### TODO Item status update
An item is retrieved from the database based on the supplied id. A status update to "done" or "not done" is allowed if the status is not the same as the requested and the item status is not "past due". If the item status is "not done" and the current date is equal or greater than the due date, the item will be saved on the database as "past due" and the description update will be not allowed as well.

#### Obtaining TODO Items and their details
An item could be got through its id, through pagination considering all items or pagination considering all items with status "not done".

---

## Tech Stack

This project uses the following technologies

- Spring Boot 3.2.3
    - Data JPA
    - Web
    - Validation
    - Test
- H2 as Database
- Lombok
- Springdoc OpenAPI

### Minor dependencies and plugins

- Gradle Test Logger Plugin - to log automatic tests results using gradle
- Google Container Tools Jib - to build docker images with gradle
- Awaitility - to test the scheduled job configuration

---

## Running the service locally

The dependencies to run the service are JDK 17 and Docker. Docker is optional since you can run the service with the command ``make gradle-run``.

### Run the automatic tests
```
make test
```
Clean the build and run all the automatic tests.

### Run the service
```
make run
```
Clean the application build, build a Docker Image and run it exposing the port 8080 of the container. The Docker must be running before the command execution.

### Run the service with gradle (Docker not needed)
```
make gradle-run
```
Clean the build and run the application with gradle. No Docker running is necessary to run the application with this target.