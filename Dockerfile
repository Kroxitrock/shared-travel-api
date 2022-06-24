FROM openjdk:11.0.12-jdk
VOLUME /tmp
COPY target/app-0.0.1-SNAPSHOT.jar app-0.0.1-SNAPSHOT.jar
CMD java -jar /app-0.0.1-SNAPSHOT.jar