# Demo Twins

[![Build](https://img.shields.io/badge/build-maven-blue)](https://github.com/) [![Java](https://img.shields.io/badge/Java-17%2B-orange)](https://www.oracle.com/java/) [![Docker Compose](https://img.shields.io/badge/docker--compose-ready-blue)](https://docs.docker.com/compose/) [![Kafka](https://img.shields.io/badge/Kafka-Confluent-orange?logo=apachekafka)](https://kafka.apache.org/) [![Postgres](https://img.shields.io/badge/Postgres-Ready-blue?logo=postgresql)](https://www.postgresql.org/) [![TimescaleDB](https://img.shields.io/badge/TimescaleDB-Ready-blue?logo=timescaledb&logoColor=white)](https://www.timescale.com/) [![Redis](https://img.shields.io/badge/Redis-Ready-red?logo=redis)](https://redis.io/) [![Prometheus](https://img.shields.io/badge/Prometheus-Ready-orange?logo=prometheus)](https://prometheus.io/) [![Grafana](https://img.shields.io/badge/Grafana-Ready-blue?logo=grafana)](https://grafana.com/)

**Demo Twins** is a Java Spring Boot demo that implements a digital‑twin pattern for drill‑bit sensors. The project consumes sensor events from Kafka (Avro + Confluent Schema Registry), keeps fast-access state in Redis, writes time-series measurements to TimescaleDB, and stores domain snapshots in PostgreSQL.

---

## Table of contents

- [Architecture](#architecture)
- [Project Structure (high level)](#project-structure-high-level)
- [Requirements](#requirements)
- [Quick Commands](#quick-commands)
- [Local Quick Start (full stack)](#local-quick-start-full-stack)
- [Producer simulator (Avro)](#producer-simulator-avro)
- [Schema registry & Avro tooling](#schema-registry--avro-tooling)
- [Example payload & topics](#example-payload--topics)
- [Observability](#observability)
- [Notes](#notes)

## Quick Commands

- Start infra: `docker compose -f compose.yaml up -d`
- Create Kafka topics: `./scripts/create-kafka-topics.sh`
- Build: `./mvnw -DskipTests package`
- Run: `./mvnw spring-boot:run`
- Run simulator (one-shot): `RUN_ONCE=1 python scripts/producer_simulator.py`

---

## Architecture

This project follows a Hexagonal (Ports & Adapters) architecture: application ports define use-cases, domain contains business logic and models, and infrastructure provides adapters (Kafka, Redis, JPA, Timescale).

## Project Structure (high level)

- **Application (ports & runtime):** `src/main/java/com/twins/demo_twins/application`
- **Domain (core model):** `src/main/java/com/twins/demo_twins/domain`
- **Infrastructure (adapters):** `src/main/java/com/twins/demo_twins/infrastructure` (Kafka consumer `SensorEventListener`, Redis adapter, persistence adapters)
- **Avro schemas:** `src/main/avro` (`EventEnvelopeAvro.avsc`, `SensorEventAvro.avsc`)
- **Scripts:** `scripts/` — helpers to create Kafka topics and run the producer simulator

---

## Requirements

- Java 17+
- Docker & Docker Compose
- Maven (`mvnw` included)
- Python 3.8+ to run the simulator (recommended packages below)

---

## Local Quick Start (full stack)

1. Start infra services:

```bash
docker compose -f compose.yaml up -d
```

2. Create Kafka topics:

```bash
./scripts/create-kafka-topics.sh
# or on PowerShell
./scripts/create-kafka-topics.ps1
```

3. Ensure Avro schemas are registered in Schema Registry (used by the UI and producers/consumers). You can use the Maven plugin configured in this project or the Schema Registry UI/API.

4. Build & run the app:

```bash
# Build
./mvnw -DskipTests package
# Run
./mvnw spring-boot:run
# or
java -jar target/*.jar
```

---

## Producer simulator (Avro)

The simulator serializes Avro and uses Schema Registry. Install dependencies from:

```bash
pip install -r scripts/producer-requirements.txt
```

Quick start (Windows PowerShell):

```powershell
python -m venv .venv
.venv\Scripts\Activate.ps1
pip install -r scripts/producer-requirements.txt
python scripts\producer_simulator.py
```

(Unix):

```bash
python3 -m venv .venv
source .venv/bin/activate
pip install -r scripts/producer-requirements.txt
python3 scripts/producer_simulator.py
```

The simulator supports a one-shot run (`RUN_ONCE=1`) for quick tests and will register or reference payload schemas when necessary.

---

## Example payload & topics

- Topics:
  - `sensor-events` — main topic for sensor events
  - `sensor-events-dlt` — dead-letter topic for failed messages
- Subjects (Schema Registry, Confluent Avro serializer):
  - `sensor-events-value` (value subject for `sensor-events`)

Example message (EventEnvelope JSON):

```json
{
	"eventId": {
		"string": "d0014463-2889-4114-95c5-4deaa044c7e1"
	},
	"eventType": "SensorEvent",
	"timestamp": {
		"long": "2026-02-03T17:16:53.750Z"
	},
	"payload": {
		"SensorEventAvro": {
			"assetId": "drill-bit-1",
			"sensorId": "pressure-1",
			"value": 34.02,
			"type": "PRESSURE",
			"eventTime": "2026-02-03T17:16:53.750Z"
		}
	}
}
```

You can list registered subjects with: `curl http://localhost:8081/subjects`

---

## Schema registry & Avro tooling

This project includes Avro schemas in `src/main/avro` and provides tools to generate Java classes and register schemas to Confluent Schema Registry.

- Generate Avro Java classes:
  - `./mvnw -DskipTests generate-sources` (or `./mvnw -DskipTests package`).
  - Generated classes are placed in `target/generated-sources/avro` and will be compiled into the application.

- Register schemas to Schema Registry (Confluent Maven plugin):

```bash
# explicit plugin invocation (works even without plugin in pom)
./mvnw -U io.confluent:kafka-schema-registry-maven-plugin:8.1.1:register -Dschema.registry.url=http://localhost:8081

# or, invoke the configured plugin goal directly 
./mvnw -U io.confluent:kafka-schema-registry-maven-plugin:register -Dschema.registry.url=http://localhost:8081
```

Use `-U` to force Maven to refresh plugin/dependency metadata after a previous resolution failure cached in the local repository.
For topic `sensor-events`, the main value subject is `sensor-events-value` (envelope schema). The `sensor-event-value` subject is kept as a referenced payload schema.

- Useful Schema Registry HTTP commands:
  - List subjects: `curl http://localhost:8081/subjects`
  - List versions for a subject: `curl http://localhost:8081/subjects/<subject>/versions`
  - Get schema by id: `curl http://localhost:8081/schemas/ids/<id>`

---

## Observability

- **Prometheus** is exposed at `http://localhost:9090` (compose service `prometheus`), scraping the Spring Boot actuator at `host.docker.internal:8080/actuator/prometheus` by default.
- **Grafana** is available at `http://localhost:3000` (compose service `grafana`). Dashboards can be configured to read the Prometheus datasource.

---

## Notes

- DB schema changes are managed with Liquibase under `src/main/resources/db/changelog`.
- Kafka handling and DLQ logic is implemented under `infrastructure/kafka`.
