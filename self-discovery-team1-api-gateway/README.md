# API Gateway mit automatischer Service Discovery
## Einleitung
Dieses Projekt entsteht im Rahmen unseres Informatikprojekts an der TH Köln. Die Anforderungen die an uns gestellt wurden, sind unter [IP/PP/BA: API Gateway mit automatischer Service Discovery
](https://blogs.gm.fh-koeln.de/bente/2018/03/10/ipppba-api-gateway-mit-automatischer-service-discovery/) zu finden. Die vollständige Dokumentation zu diesem Projekt befindet sich in Confluence in dem Zweig [
Team 1 SS2018](https://fsygs15.gm.fh-koeln.de/confluence/display/ARL/Team+1+SS2018).

Dieses Repository beinhaltet unser Haupt-Projekt das API-Gateway. Eine gesammte Umgebung besteht in diesem Projekt aus den drei Komponenten:
- Microservice(s) - [self-discovery-team1-backend](https://fsygs15.gm.fh-koeln.de:8888/ArchiLab/self-discovery-team1-backend)
- API-Gateway - [self-discovery-team1-api-gateway](https://fsygs15.gm.fh-koeln.de:8888/ArchiLab/self-discovery-team1-api-gateway)
- Service Discovery - [self-discovery-team1-service-discovery](https://fsygs15.gm.fh-koeln.de:8888/ArchiLab/self-discovery-team1-service-discovery)

Da die drei Systeme kommunizieren müssen, haben wir uns Einfachheit halber dazu entschlossen die Ports für die Kommunikation in dem Beitrag [Umsetzung Spring Cloud Gateway](https://fsygs15.gm.fh-koeln.de/confluence/display/ARL/6.+Umsetzung+Spring+Cloud+Gateway#id-6.UmsetzungSpringCloudGateway-Technisches) zu definieren.

## In Betriebnahme

Nach dieser Anleitung ist eine Umgebung mit unserer [Automatischen Service Discovery mit Aggregate Root](https://fsygs15.gm.fh-koeln.de/confluence/display/ARL/Automatische+Service+Discovery+mit+Aggregate+Root) und [Bereitgestellte Einstiegspunkte aller am Gateway angemeldeten Microservices](https://fsygs15.gm.fh-koeln.de/confluence/display/ARL/Bereitgestellte+Einstiegspunkte+aller+am+Gateway+angemeldeten+Microservices) in Betrieb genommen.

### 1. Kompilieren und Paketieren
Wir setzen bei allen unseren Projekten auf das Build-Management-Tool Maven. Damit können wir unabhängig von Entwicklungsumgebungen sehr einfach Abhängigkeiten definieren. Die jeweiligen Entwicklungsumgebungen mit Maven-Support wissen dann damit umzugehen.

Der nachfolgende Befehl im Hauptverzeichnis dieses Repositories kompiliert das Projekt und paketiert es als JAR-Datei.
```
mvn package
```

### 2. Ausführung
Anschließend kann man die JAR-Datei direkt auf unserem Betriebssystem mit

```
java -jar target/cloudGateway-1.0-SNAPSHOT.jar
```

ausführen. Das kann man auch mit den anderen beiden Projekten manuell machen.

## Kontainerisierung
Wir haben uns entschieden die Umgebung mit Docker und Docker-Compose zur Verfügung zu stellen.

Dafür ist hier die komplette Schritt für Schritt Anleitung.

### 1. Alle Projekte auschecken

```
cd ~

git clone git@fsygs15.gm.fh-koeln.de:ArchiLab/self-discovery-team1-api-gateway.git

git clone git@fsygs15.gm.fh-koeln.de:ArchiLab/self-discovery-team1-service-discovery.git

git clone git@fsygs15.gm.fh-koeln.de:ArchiLab/self-discovery-team1-backend.git
```

Man kann natürlich auch die HTTP(S)-Variante zum Auschecken nehmen.

### 2. Mit Maven bauen und paketieren

```
cd ./self-discovery-team1-api-gateway
mvn clean dependency:copy-dependencies package

cd ../self-discovery-team1-service-discovery
mvn clean dependency:copy-dependencies package

cd ../self-discovery-team1-backend
mvn clean dependency:copy-dependencies package
```

### 3. Docker-Images bauen
```
cd ../self-discovery-team1-api-gateway
docker build . -t self-discovery-team1-api-gateway

cd ../self-discovery-team1-service-discovery
docker build . -t self-discovery-team1-service-discovery

cd ../self-discovery-team1-backend
docker build . -t self-discovery-team1-backend
```

### 4. Eigenes virtuelles Netzwerk erstellen
Damit alle unsere Services untereinander kommunizieren können, erstellen wir ein eigenes Netzwerk für unsere Services.

```
docker network create team1-network
```
Damit verhindern wir zum einen, dass die Services mit bestehenden in Konflikt kommen und zum anderen ersparen wir uns viele Ports unnötig rauszugeben und diese zu pflegen.

### 5. Umgebung hochfahren
```
cd ../self-discovery-team1-api-gateway
docker-compose run -p 8888:8888 api-gateway

```

Es sind nur die Services API-Gateway und Service Discovery von außen erreichbar.

Warten bis die Services gestartet sind, kann mit dem Browser überprüft werden:

- localhost:8761 -> Service Discovery
- localhost:8888 -> API-Gateway

Gerne auch überprüfen, dass in dem Service Discovery und dem API-Gateway auch die Routen zu unserem Backend aufgeführt sind.

### 6. Weitere Instanz von MS1 hochfahren
```
docker run -e "SPRING_APPLICATION_NAME=ms1" -e "EUREKA_CLIENT_SERVICE_URL_DEFAULT_ZONE=http://service-discovery:8761/eureka" --network team1-network self-discovery-team1-backend
```
Überprüfen, dass nach dem Start dieser Service als zweite Instanz in der Service-Discovery aufgeführt ist.

### 7. Eine weitere Instanz vom Backend als MS2 hochfahren
```
docker run -e "SPRING_APPLICATION_NAME=ms2" -e "EUREKA_CLIENT_SERVICE_URL_DEFAULT_ZONE=http://service-discovery:8761/eureka" --network team1-network self-discovery-team1-backend
```

Erstellt einen neuen Microservice-Eintrag in der Service Discovery (also das Automatische Discovery).

### 8. ArchiLab-Services testen
Dafür muss in der local-Konfiguration eines MS von Archilab der Pfad zur unserer Service Discovery gesetzt werden und beim Ausführen muss das eigene Netzwerk zugeordnet werden.

Das verdeutlichen wir mal am learningoutcome-ms:

application-local.yml
```
spring:
  jpa:
    hibernate:
      ddl-auto: create-drop
  h2:
    console:
      enabled: true
#Kafka
  kafka:
    bootstrap-servers: localhost:9090
# configure eureka client
eureka:
  client:
    enabled: true
  service-url:
    defaultZone: http://service-discovery:8761/eureka

server:
  port: 8085
```

Dockerfile
```
FROM openjdk:8-jre-alpine

EXPOSE 8085

COPY learningoutcome-ms-0.0.1-SNAPSHOT.jar app-learningoutcome.jar

ENTRYPOINT ["java","-Xms128m","-Xmx256m","-Djasypt.encryptor.password=_ArchiLabLearningMS_2017","-Dspring.profiles.active=local","-Djava.security.egd=file:/dev/./urandom","-jar","/app-learningoutcome.jar" ]
```

Docker-Image erstellen:

```
cd ./learningoutcome-ms
docker build . -t self-discovery-team1-learningoutcome-ms
```

Docker-Container starten:
```
docker run --network team1-network self-discovery-team1-learningoutcome-ms
```

### Aufräumen:
```
docker rmi -f self-discovery-team1-backend
docker rmi -f self-discovery-team1-service-discovery
docker rmi -f self-discovery-team1-api-gateway

docker network rm team1-network
```

## Endpunkte
Normalerweise stellt, das API-Gateway selber keinen Endpunkt zur Verfügung. Spring bietet zum Monitoring und zur Verwaltung bereits einige Möglichkeiten an, außerdem haben auch wir das API-Gateway noch um eine weiteren Entpunkt erweitert.

### Actuator
Über das Package spring-boot-starter-actuator werden bereits einige (2 bis 19) actuator - Endpunkte zur Verwaltung und zum Monitoring zur Verfügung gestellt.

Die für uns wichtigste ist: 

```
/actuator/gateway/routes
```

### Geroutete Aggregate Roots
Wir haben ein Endpunkt für "/" hinzugefügt, bei diesem Endpunkt wird aber je nach Accept-Header eine andere "Repräsentation" zurückgeliefert.

#### HAL-Browser
```
Accept=text/html
```

Weiterleitung nach browser.html

Da wir kein Spring Data Rest nutzen, wird der HAL-Browser nicht automatisch gemappt/ausgeführt.

Aktuell ist der HAL-Browser in das `resources/public`-Verzeichnis kopiert und wird somit als statische Ressource von Spring Boot ausgeliefert.

#### HAL+JSON
```
Accept=application/hal+json,application/json
```
Ausgabe aller erreichbaren Aggregate-Roots die von dem API-Gateway geroutet werden.