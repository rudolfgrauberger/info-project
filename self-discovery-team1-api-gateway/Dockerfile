FROM openjdk:8-jre-alpine

LABEL authors="timo.gerth@smail.th-koeln.de, rudolf.grauberger@smail.th-koeln.de"

VOLUME /tmp

EXPOSE 8888

ARG JAR_FILE=target/cloudGateway-1.0-SNAPSHOT.jar

ADD ${JAR_FILE} cloudGateway.jar

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom", "-Deureka.client.service-url.defaultZone=${EUREKA_CLIENT_SERVICE_URL_DEFAULT_ZONE}", "-jar","/cloudGateway.jar"]
