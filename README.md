[![Build Status](https://beta-jenkins.mcc.schubergphilis.com/buildStatus/icon?job=cosmic-microservices/0001-cosmic-microservices-master-build)](https://beta-jenkins.mcc.schubergphilis.com/job/cosmic-microservices/job/0001-cosmic-microservices-master-build/)

# Cosmic Microservices

- [Cosmic Config Server](cosmic-config-server) (powered by Spring Cloud Config Server)
- [Cosmic Metrics Collector](cosmic-metrics-collector) (powered by Spring Boot)
- [Cosmic Usage API](cosmic-usage-api) (powered by Spring Boot)
- [Cosmic Bill Viewer](cosmic-bill-viewer) (powered by Spring Boot)

## Requirements

### Software requirements:

- Java 8
- Git
- Maven
- Docker


### Docker:

Please create a Docker network called `cosmic-network`.

    docker network create cosmic-network


## [Cosmic Config Server](cosmic-config-server)

Locally it runs by default at [http://localhost:7001](http://localhost:7001).
Local debugging port is `8001`.


Depends on:
- Cosmic Vault (powered by Hashicorp Vault)

## [Cosmic Metrics Collector](cosmic-metrics-collector)

Local debugging port is `8002`.

Depends on:
- [Cosmic Config Server](cosmic-config-server)
- Cosmic Database (powered by MariaDB)


## [Cosmic Usage API](cosmic-usage-api)

Locally it runs by default at [http://localhost:7003](http://localhost:7003).
Local debugging port is `8003`.

Depends on:
- [Cosmic Config Server](cosmic-config-server)
- Cosmic Metrics Index (powered by Elasticsearch)


## [Cosmic Bill Viewer](cosmic-bill-viewer)

Locally it runs by default at [http://localhost:7004](http://localhost:7004).
Local debugging port is `8004`.

Depends on:
- [Cosmic Usage API](cosmic-usage-api)
