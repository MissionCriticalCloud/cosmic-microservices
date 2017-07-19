# Cosmic Bill Viewer

Cosmic Bill Viewer is a web interface for visualizing billing reports based on the Usage API.

## Building from Source

Requirements:
- Java 8
- Git
- Maven
- Docker
- Bower

In order to build and run the Cosmic Bill Viewer, please follow the following steps:

    git clone git@github.com:MissionCriticalCloud/cosmic-microservices.git
    cd cosmic-microservices/cosmic-bill-viewer

Build a Docker image for the Cosmic Bill Viewer:

    mvn clean package \
        -P local -DskipTests

Start a Docker container running the Cosmic Bill Viewer:

    docker run -it --rm \
        --network cosmic-network \
        --name cosmic-bill-viewer \
        missioncriticalcloud/cosmic-bill-viewer

Alternatively, if you want to access the container locally:

    docker run -it --rm -p 7004:8080 -p 8004:8000 \
        --network cosmic-network \
        --name cosmic-bill-viewer \
        missioncriticalcloud/cosmic-bill-viewer

The service should be available at: [http://localhost:7004/](http://localhost:7004/)

One-liner:

    mvn clean package \
        -P local -DskipTests && \
    docker run -it --rm -p 7004:8080 -p 8004:8000 \
        --network cosmic-network \
        --name cosmic-bill-viewer \
        missioncriticalcloud/cosmic-bill-viewer
