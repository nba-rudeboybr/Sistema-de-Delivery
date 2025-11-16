FROM openjdk:17-jdk-slim

WORKDIR /app

COPY pom.xml .
COPY src ./src
COPY target/order-service-0.0.1-SNAPSHOT.jar .

EXPOSE 8080

CMD ["java", "-jar", "order-service-0.0.1-SNAPSHOT.jar"]

