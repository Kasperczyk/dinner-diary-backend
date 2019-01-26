# Dinner Diary Backend

This is the backend for my [dinner-diary-webapp](https://github.com/Kasperczyk/dinner-diary-webapp).

## Motivation
My motivation is twofold: personal and professional.

### Personal motivation
My wife and I have been cooking three course menus almost every weekend for each other (alternating) for some time now.
After a while you start to lose track of what recipes you've already used and what you thought was really good or not so much.
We have a rule of not cooking the same dish a second time except when cooking for other people; in which case it would
be really good to know what we cooked before and liked to make menu planning easier.

### Professional motivation
I wanted to have a non-trivial, working application for years now and never really got around to it.
I can use the project as a playground to try out new things, to get and test ideas for a blog I'm planning to create soon(ish&trade;),
and as a showcase of what I can do for possible future clients.

## Technologies used
- Java 8
- Spring Framework / Spring Boot
- PostgreSQL
- H2 Database
- Hibernate
- Flyway
- Lombok
- Modelmapper
- java-jwt
- Hamcrest
- Jackson

## How to run locally
You basically have two options:
1. use the default profile which uses H2 (which I only use for automated tests)
2. install a local postgres server and create a 'dinner-diary' database for user 'dinner-diary' and password 'dinner-diary'

In both cases flyway automatically created the schema.
If you use the default profile with H2 keep in mind that you lose everything when you restart the backend (in memory database).

You can communicate with (i.e. send requests to) the backend via 'localhost:8080/dinner-diary'.

If you want to start the app from your terminal use the spring boot maven plugin

    mvn clean install
    mvn spring-boot:run
    
or run it as a packaged application

    mvn clean install
    java- jar target/dinner-diary-backend-<version>.jar

If you want to start the app from your favorite IDE chances are you know how to do it.

## Deployment
I'll probably deploy this on AWS some time in the future, but it hasn't happened yet.