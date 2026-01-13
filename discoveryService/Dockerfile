FROM eclipse-temurin:17-jdk-jammy

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file from your Spring Boot project
COPY target/discoveryService-0.0.1-SNAPSHOT.jar /app/discovery-service.jar

# Expose the port Eureka Server typically runs on (default is 8761)
EXPOSE 8761

# Command to run the Spring Boot application
ENTRYPOINT ["java","-jar","/app/discovery-service.jar"]

