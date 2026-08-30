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

Then fill in the required credentials.

## Start Infrastructure
```bash
docker compose up -d
```
## Initialize Apache Ignite

Explain the Ignite CLI command and:
```bash
cluster init --name=ignite3
```
Mention this is only required for a new Ignite volume.

## Build
```bash
set -a
source .env
set +a
./gradlew clean build
```
## Run
```bash
./gradlew bootRun
```
## Application

Frontend:
http://localhost:8080/

Swagger:
http://localhost:8080/swagger-ui.html

## Main Features
- Home/appliance registration
- Kafka-based telemetry
- Ignite real-time metrics
- PostgreSQL historical storage
- Budget warning and penalty tariffs
- Appliance anomaly detection
- Gemini-generated Turkish recommendations
- Email notifications
- Live frontend polling and charts
