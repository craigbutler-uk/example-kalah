# Example Kalah

This project implements a simple REST service to run a game of Kalah.

See  <https://en.wikipedia.org/wiki/Kalah> for an explanation of the rules.

## Installation

This application is built using Spring Boot. It is intended to run with an external MySQL database but it can be run with an in-memory H2 database if MySQL is not present.

Run with MySQL:

```
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

Run with in-memory DB:

```
mvn spring-boot:run
```

## Usage

A game is started using a POST command e.g.

```
curl --location --request POST 'http://localhost:8080/games'
```

Moves are made using a PUT:

```
curl --location --request PUT 'http://localhost:8080/games/1/pits/6'
```

A GET is implemented to check the progress of a game:

```
curl --location --request GET 'http://localhost:8080/games/1'
```

## Technology

### Spring Boot
The application was initially created at <https://start.spring.io/>. It uses Java 11.

### Database

The game is persisted into a database using JPA/Hibernate and a JPA repository.

The initial database schema is built using Flyway, which runs a SQL script at startup. Hibernate's auto DDL is disabled.

The database entities are deliberately different to the REST DTO, so we need to translate from one to the other. In a larger application, we would use something like ModelMapper to do this.

The list of pits in the database is 0-based whereas the list of pits in the REST API is 1-based.

### Swagger

The REST API is documented using Swagger. The Swagger UI can be accessed at 
<http://localhost:8080/swagger-ui/#/game-controller>

As well as providing documentation, the API can be tested from within the Swagger UI ('Try it out', 'Execute'). 

### Tests

A JUnit/Mockito test provides a test of the game logic. 

A Spring MvcTest tests the REST API.

A Spring JpaTest tests the persistence.

## About the game

The app is a simple implementation of Kalah. It uses a single list of pits to represent the board. 

It does not have a concept of users, rather it uses the concept of a South turn and a North turn. The game always starts with it being South's turn and only stones in pits in the range 1 to 6 can be moved. When it is North's turn, only stones in pits 8 to 13 can be moved. Attempts to move stones from a pit which is not valid for the turn will result in an error. Using invalid IDs for the game ID or pit, as well as attempting to move stones from empty pits, will result in an error.

There is no concept of users - anybody can issue a request for a move but it is only executed if the pit selected is valid for the current turn.


