# Cosmic Usage API

Cosmic Usage API is a Spring Boot application that provides usage information about Cosmic resources.

## Building from Source

Requirements:
- Java 8
- Git
- Maven
- Docker

JVM Options:
- `-Duser.timezone=UTC`

In order to build and run the Cosmic Usage API, please follow the following steps:

    git clone git@github.com:MissionCriticalCloud/cosmic-microservices.git
    cd cosmic-microservices/cosmic-usage-api

Build a Docker image for the Cosmic Usage API:

    mvn clean package \
        -P local -DskipTests

Start a Docker container running the Cosmic Usage API:

    docker run -it --rm \
        --network cosmic-network \
        --name cosmic-usage-api \
        missioncriticalcloud/cosmic-usage-api

Alternatively, if you want to access the container locally:

    docker run -it --rm -p 7003:8080 -p 8003:8000 \
        --network cosmic-network \
        --name cosmic-usage-api \
        missioncriticalcloud/cosmic-usage-api

In that case, the service should be available at: [http://localhost:7003/](http://localhost:7003/)

One-liner:

    mvn clean package \
        -P local -DskipTests && \
    docker run -it --rm -p 7003:8080 -p 8003:8000 \
        --network cosmic-network \
        --name cosmic-usage-api \
        missioncriticalcloud/cosmic-usage-api
