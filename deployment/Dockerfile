FROM eclipse-temurin:21-jdk-alpine
WORKDIR /app
COPY ./applications/app-service/build/libs/requests.jar .

# Directorio para llaves JWT
RUN mkdir -p /keys
VOLUME /keys

EXPOSE 8081
ENTRYPOINT ["java","-jar","requests.jar"]
