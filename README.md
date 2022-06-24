# Shared Travel API

## Prerequisites

1. Install [Java 11](https://adoptopenjdk.net/) 
1. Install [Maven](https://maven.apache.org/download.cgi). You need to put both M2_HOME and MAVEN_HOME
1. Install [Lombok Plugin](https://projectlombok.org/setup/intellij) on your IDE of choice

## Installing the project

1. Clone the Gitlab repository
1. Run `./mvnw compile`

## Running the project as a service

1. Run `./mvnw clean spring-boot:run`

## Running the project as a container

1. Run `./mvnw clean package` to produce a .jar file
1. Run `docker build -t shared-travel-app .` to build the container
1. Run `docker run -p 8080:8080 shared-travel-app` to deploy the container
