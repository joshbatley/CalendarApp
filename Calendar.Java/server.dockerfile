FROM openjdk:23-slim

WORKDIR /app

COPY pom.xml .
COPY src ./src

RUN apt-get update && \
    apt-get install -y maven

RUN mvn clean install

CMD ["java", "-jar", "target/calendar-server-0.0.1-SNAPSHOT.jar"]
