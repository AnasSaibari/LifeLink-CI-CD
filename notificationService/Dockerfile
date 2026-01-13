FROM eclipse-temurin:17-jdk-jammy

# Set the working directory inside the container
WORKDIR /app

# Copy the built JAR file from your Spring Boot project
COPY target/notificationService-0.0.1-SNAPSHOT.jar /app/notification-service.jar

# Expose the port
EXPOSE 9098

# Command to run the Spring Boot application
ENTRYPOINT ["java","-jar","/app/notification-service.jar"]