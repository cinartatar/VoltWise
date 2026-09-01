# VoltWise
VoltWise is a household energy monitoring platform built with Spring Boot.

It receives appliance telemetry through Apache Kafka, stores live metrics in Apache Ignite, stores persistent data in PostgreSQL, and provides budget/anomaly notifications through email and Gemini-generated recommendations.

## Technologies

- Java 17
- Spring Boot
- PostgreSQL
- Apache Kafka
- Apache Ignite
- Gemini API
- HTML / CSS / JavaScript
- Docker Compose

## Requirements

Before running the project, install:

- Java 17
- Docker
- Docker Compose

## Environment Variables

Create a local `.env` file based on `.env.example`.

```bash
cp .env.example .env
```

Then fill in the required credentials and configuration values.

The application uses environment variables for PostgreSQL, Kafka, Ignite, email, tariff configuration, scheduling, and Gemini API access.

## Start Infrastructure
```bash
docker compose up -d
```
You can check that the containers are running with:
```bash
docker compose ps
```

## Initialize Apache Ignite

Apache Ignite must be initialized once when using a new Ignite data volume.

Open the Ignite CLI:
```bash
docker run --rm -it --network=host \
  -e LANG=C.UTF-8 \
  -e LC_ALL=C.UTF-8 \
  apacheignite/ignite:3.1.0 cli
```

Then initialize the cluster:
```bash
cluster init --name=ignite3
```
This step is only required for a new Ignite volume.

If the Ignite data volume already contains an initialized cluster, it does not need to be initialized again.

The Spring Boot application automatically creates the required Ignite tables on startup.

To access the Ignite SQL console from the CLI, use:
```bash
sql
```

## Build
Load the variables from .env into the current terminal:
```bash
set -a
source .env
set +a
```
Then build the project:
```bash
./gradlew bootRun
```

## Run
```bash
set -a
source .env
set +a
```
Then run the Spring Boot application.

## Application

Frontend:
http://localhost:8080/

Swagger:
http://localhost:8080/swagger-ui.html

## Main Features
- Home and appliance registration
- Kafka-based appliance telemetry
- Apache Ignite real-time metrics
- PostgreSQL persistent and historical storage
- Budget usage monitoring
- Budget warning thresholds
- Dynamic penalty tariffs
- Appliance anomaly detection
- Gemini-generated Turkish recommendations
- Email notifications
- Live frontend polling
- Appliance-level metrics
- Household-level metrics
- Historical consumption charts

## Architecture
                 ┌─────────────────┐
                 │     Web App     │
                 │ HTML / CSS / JS │
                 └────────┬────────┘
                          │ REST
                          ▼
                 ┌─────────────────┐
                 │   Spring Boot   │
                 │      Core       │
                 └───┬────┬────┬──┘
                     │    │    │
             ┌───────┘    │    └─────────┐
             ▼            ▼              ▼
      ┌────────────┐ ┌───────────┐ ┌────────────┐
      │ PostgreSQL │ │  Ignite   │ │   Gemini   │
      │ Persistent │ │Live Data  │ │     API    │
      └────────────┘ └───────────┘ └────────────┘
                           ▲
                           │
                         Kafka
                           ▲
                           │
                    ┌─────────────┐
                    │  Telemetry  │
                    │   Sensors   │
                    └─────────────┘

## Registration Flow
Homes and their appliances can be registered through the REST API or Swagger UI.

When a home is registered:

1. The home is stored in PostgreSQL.
2. Its appliances are stored in PostgreSQL.
3. An asset registration event is published to Kafka.
4. The telemetry sensor receives the registration event.
5. The sensor begins producing appliance readings.
6. The backend processes telemetry and stores live metrics in Apache Ignite.

## Data Storage
PostgreSQL is used for persistent information such as:

Homes
- Appliances
- Daily consumption history
- Notification-related persistent data

Apache Ignite is used for frequently updated real-time information such as:

- Current power consumption
- Accumulated energy
- Accumulated cost
- Budget percentage
- Tariff state
- Appliance anomaly information

## Stopping the Application

Stop the Spring Boot application with:
```bash
Ctrl+C
```
Stop the Docker infrastructure with:
```bash
docker compose down
```
Do not normally use:
```bash
docker compose down -v
```
The -v option removes Docker volumes and deletes persistent PostgreSQL and Ignite data. If the Ignite volume is deleted, the Ignite cluster must be initialized again the next time the project is started.
## Notes

The .env file contains local credentials and should not be committed to Git.

Use .env.example as the template for configuring a new installation.