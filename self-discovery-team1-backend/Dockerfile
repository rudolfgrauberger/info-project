FROM openjdk:8-jre-alpine

LABEL authors="timo.gerth@smail.th-koeln.de, rudolf.grauberger@smail.th-koeln.de"

VOLUME /tmp

EXPOSE 8080
ARG JAR_FILE=target/gs-spring-boot-0.1.0.jar
ADD ${JAR_FILE} backend.jar

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-Dspring.application.name=${SPRING_APPLICATION_NAME}", "-Dserver.port=8080", "-Deureka.client.service-url.defaultZone=${EUREKA_CLIENT_SERVICE_URL_DEFAULT_ZONE}", "-jar","/backend.jar"]
