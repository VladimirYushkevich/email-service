email-service [![Build Status](https://travis-ci.org/VladimirYushkevich/email-service.svg?branch=master)](https://travis-ci.org/VladimirYushkevich/email-service) [![codecov](https://codecov.io/gh/VladimirYushkevich/email-service/branch/master/graph/badge.svg)](https://codecov.io/gh/VladimirYushkevich/email-service)
=
### Description:

MicroService responsible of Asynchronous sending of Emails. It will be used by
various other components to send mail to end customers.

### Requirements

- REST API with synchronous acknowledgment with only one method for sending new Mail
- Mail with attachment should be possible. (Attachment Content will be provided in the request by a
URI pointing to the actual document binaries)
- Queuing until successful response from SMTP Server. Max Retry configurable.
- No Authentication required.

### Run kafka container ([Instructions](http://wurstmeister.github.io/kafka-docker/))
- You have installed Docker/Compose
- Clone repo with docker files:
```
git clone https://github.com/wurstmeister/kafka-docker
cd kafka-docker
```
- Edit `docker-compose.yml`:
```
environment:
    KAFKA_ADVERTISED_HOST_NAME: docker.for.mac.localhost
    KAFKA_CREATE_TOPICS: "Email:1:1"
```
  Expose port `9092` of `kafka` service to the host i.e. change it to `9092:9092`
- Run kafka container:
```
docker-compose up
```

### Run service:
```
./gradlew clean build -i && java -jar build/libs/email-service-0.0.1-SNAPSHOT.jar
```
### The Task
```
curl -X POST localhost:8080/api/v1/email -d '{"from": "from@example.com", "subject": "Subject"}' -H 'Content-Type: application/json' | jq
```
### Usage:

[SWAGGER](http://localhost:8080/swagger-ui.html)