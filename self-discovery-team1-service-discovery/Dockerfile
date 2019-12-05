FROM openjdk:11
LABEL authors="timo.gerth@smail.th-koeln.de"
VOLUME /tmp
EXPOSE 8761
ARG JAR_FILE=target/eureka-service-1.0.0.jar
ADD ${JAR_FILE} eureka-service.jar
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/eureka-service.jar"]