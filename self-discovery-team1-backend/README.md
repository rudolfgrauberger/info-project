# Beispiel Microservice
## Einleitung
Dieses Repository beinhaltet ein Microservice mit einem REST-Endpunkt, mit dem wir in erster Instanz unsere eigentliche Aufgabe das API-Gateway testen.

## Einrichtung (manuelles Kompilieren/IntelliJ)
Da wir mehrere Instanzen (für das LoadBalancing) dieses Microservices benötigen, haben aktuell zwei Profile angelegt.

Damit man diese Ausführen kann, müssen Build-Profile angelegt werden, die dann die jeweilige Instanz mit dem Profil starten.

Dafür geht man auf **Run > Edit Configurations** und klickt in dem Dialog links oben auf die **+**(Hinzufügen)-Schaltfläche und wählt hier in der Liste **Application**. Das macht man für jede Konfiguration.

Bitte für die nachfolgenden Konfigurationen ausführen:


||Name|Main class| VM options |
|-|-----|----|----|
|1.| ms1 | wg.hub.Main | -Dspring.profiles.active=ms1 |
|2.| ms2 | wg.hub.Main | -Dspring.profiles.active=ms2 |

## Einrichtung (Docker)
Siehe Anleitung in dem Hauptprojekt [self-discovery-team1-api-gateway](https://fsygs15.gm.fh-koeln.de:8888/ArchiLab/self-discovery-team1-api-gateway)