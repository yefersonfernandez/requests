FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY ./applications/app-service/build/libs/requests.jar .

EXPOSE 8081
ENTRYPOINT ["java","-jar","requests.jar"]
