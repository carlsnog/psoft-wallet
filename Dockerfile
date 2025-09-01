FROM eclipse-temurin:17-jdk AS build

WORKDIR /app

COPY gradlew .
COPY gradle ./gradle

RUN chmod +x gradlew

COPY . .

RUN ./gradlew bootJar --no-daemon

FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY --from=build /app/build/libs/psoft-commerce.jar app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]

