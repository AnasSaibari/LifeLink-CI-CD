#STAGE 1 : BUILD
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline

COPY /src ./src
RUN mvn clean package -DskipTests

#STAGE 2 : RUN TIME
FROM eclipse-temurin:17-jre-jammy
WORKDIR /app
COPY --from=build /app/target/*.jar hospitalService.jar
EXPOSE 9093
ENTRYPOINT ["java","-jar","hospitalService.jar"]