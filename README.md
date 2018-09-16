# Vertx REST API  [![Build Status](https://travis-ci.org/mohasaid/vertx-rest-api.svg?branch=master)](https://travis-ci.org/mohasaid/vertx-rest-api)

Restful API to calculate real time statistics from the last 60 seconds of the processed transactions. The API has 3 end points:

**POST /transactions** - Registers a new transaction each time it happens with the timestamp and the amount.

**GET /statistics** - Returns the statistics (such as average, sum, maximum, minimum, count) based on the amount of the transactions from the last 60 seconds.

**DELETE /transactions** - Deletes all transactions from the last 60 seconds.

### Prerequisites
* JDK 1.8+
* Maven 3.0+ 

### Usage

To run the project:

```
$ mvn clean spring-boot:run
```

### Techonologies 

- [Spring Boot](https://spring.io/projects/spring-boot)
- [Vertx](https://vertx.io/)
- [Lombok](https://projectlombok.org/)

The project is built using spring-boot, allowing the project to be self-contained and deployed isolated from any other web container (like Jboss, Tomcat, etc.). The REST API implementation is done using vert.x Web, a framework that lets you create web applications easily using Vert.x, which allows you to build
apps that can handle a lot of concurrency using a small number of kernel threads. It also uses lombok, a java library which helps to reduce boilerplate code.


