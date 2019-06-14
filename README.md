email-service [![Build Status](https://travis-ci.org/VladimirYushkevich/email-service.svg?branch=master)](https://travis-ci.org/VladimirYushkevich/email-service) [![codecov](https://codecov.io/gh/VladimirYushkevich/email-service/branch/master/graph/badge.svg)](https://codecov.io/gh/VladimirYushkevich/email-service)
=
### Description:

MicroService responsible for Asynchronous sending of Emails. It will be used by
various other components to send mail to end customers.

### Requirements

- REST API with synchronous acknowledgment with only one method for sending new Mail
- Mail with attachment should be possible. (Attachment Content will be provided in the request by a
URI pointing to the actual document binaries)
- Queuing until successful response from SMTP Server. Max Retry configurable.
- No Authentication required.

### Features

- Schema registry supports for *avro* serialization
- Kafka streams API
- REST endpoint to fetch statistics from kafka streams materialized store  

### Run service:
```
docker-compose up
docker-compose down
```
In order to rebuild service
```
docker-compose up -d --no-deps --build email-service
```
Logs:
```
docker logs --tail email-service
```
### Example of requests
```
curl -i -X POST 'http://localhost:8888/api/v1/email' \
-H "accept: */*" \
-H "Content-Type: multipart/form-data" \
-F 'file=@docs/CodeChallenge_Java_SWAT_2018.pdf;type=application/pdf' \
-F 'email={"from": "from@example.com", "subject": "Subject"};type=application/json'
```
```
curl localhost:8888/api/v1/email/status/{id} | jq
```
There is a background thread for sending random emails in kafka. Below endpoint should return new values
every 60 sec
```
curl localhost:8888/api/v1/email/counts | jq
```
### Usage:
[SWAGGER](http://localhost:8888/swagger-ui.html)  
Multipart upload is not working [(known issue)](https://github.com/springfox/springfox-demos/issues/40)
### Environment
macOS Sierra (version 10.12.6)  
java version "1.8.0_172"  
Java(TM) SE Runtime Environment (build 1.8.0_172-b11)  
Java HotSpot(TM) 64-Bit Server VM (build 25.172-b11, mixed mode)  
