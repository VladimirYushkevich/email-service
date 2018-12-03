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

### Run service:
```
docker-compose up
docker-compose down
docker-compose up -d --no-deps --build email-service
```
### The Task
```
curl -X POST localhost:8888/api/v1/email -d '{"from": "from@example.com", "subject": "Subject"}' -H 'Content-Type: application/json'
curl -i -X POST 'http://localhost:8888/api/v1/email' \
-H 'Content-type:multipart/mixed' \
-F 'file=@/Users/uyo1787/work/private/pet/email-service/docs/CodeChallenge_Java_SWAT_2018.pdf;type=application/pdf' \
-F 'email={
  "from": "from@example.com",
  "subject": "Subject"
};type=application/json'
```
### Usage:

[SWAGGER](http://localhost:8888/swagger-ui.html)