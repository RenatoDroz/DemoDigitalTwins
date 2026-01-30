# Demo Twins

Demo Twins is a Java Spring Boot demo that implements a digital-twin pattern for drill-bit sensors. The project consumes sensor events from Kafka, keeps fast-access state in Redis, writes time-series measurements to TimescaleDB, and stores domain snapshots in PostgreSQL.

## Architecture

This project follows a Hexagonal (Ports & Adapters) architecture: application ports define use-cases, domain contains business logic and models, and infrastructure provides adapters (Kafka, Redis, JPA, Timescale).

## Project Structure (hexagonal, high level)

- **Application (ports & runtime):** `src/main/java/com/twins/demo_twins/application`
	- Contains use-case interfaces and the runtime that orchestrates twin updates (e.g. `DrillBitTwinRuntime`, `SensorEventUseCase`).
- **Domain (core model):** `src/main/java/com/twins/demo_twins/domain`
	- Entities, DTOs and events (`twin`, `event`, `dto`).
- **Infrastructure (adapters):** `src/main/java/com/twins/demo_twins/infrastructure`
	- Kafka consumer (`SensorEventListener`), Redis adapter (`RedisTwinStateAdapter`), persistence adapters for Postgres (JPA) and TimescaleDB (`TwinSnapshotWriteService`, `TimeSeriesService`).
- **Resources & DB migrations:** `src/main/resources` (includes `application.yaml` and `db/changelog` for Liquibase).
- **Scripts:** `scripts/` — helper scripts to create Kafka topics (`create-kafka-topics.sh`, `create-kafka-topics.ps1`).

Key classes: `DrillBitTwinRuntime`, `SensorEventListener`, `RedisTwinStateAdapter`, `TwinSnapshotWriteService`, `TimeSeriesService`.

## Requirements

- Java 17+ (or the Java version used by your environment)
- Docker and Docker Compose (to run Kafka, Postgres/Timescale, Redis)
- Maven (you can use the included wrappers: `mvnw` / `mvnw.cmd`)

## Local Setup

1. Start infrastructure services (Kafka, Postgres/Timescale, Redis):

```bash
docker compose -f compose.yaml up -d
```

2. Create required Kafka topics (optional scripts provided):

```bash
./scripts/create-kafka-topics.sh
# or on Windows PowerShell
./scripts/create-kafka-topics.ps1
```

3. Build the application:

```bash
# Unix
./mvnw -DskipTests package
# Windows
mvnw.cmd -DskipTests package
```

4. Run the application:

```bash
# Run with the Maven Spring Boot plugin
./mvnw spring-boot:run

# or run the packaged jar
java -jar target/*.jar
```

5. Application configuration is in `src/main/resources/application.yaml`. Adjust Kafka, DB and Redis connection settings there or via environment variables.

## Kafka message example

- Topic key (partitioning key): the `assetId` (example: `drill-bit-1`).
- Message value (JSON payload):

```json
{
	"assetId": "drill-bit-1",
	"sensorId": "pressure-1",
	"value": 80.0,
	"type": "PRESSURE",
	"eventTime": "2026-01-29T19:51:00.000Z"
}
```

Fields:
- `assetId` — unique identifier for the drill bit (used as message key).
- `sensorId` — sensor identifier.
- `value` — numeric sensor measurement.
- `type` — sensor type (`PRESSURE`, `TEMPERATURE`, ...).
- `eventTime` — ISO-8601 timestamp of the reading.

## Sensor event simulator

A simple Python script is provided at `scripts/producer_simulator.py` to send simulated sensor events to Kafka. Use it to test the application locally by producing messages to the `sensor-events` topic.

### Requirements
- Python 3.8+
- Install the Kafka client library: `pip install kafka-python`

### Quick start (Windows PowerShell)
```powershell
python -m venv .venv
.venv\Scripts\Activate.ps1
pip install kafka-python
python scripts\producer_simulator.py
```

### Quick start (Unix / macOS)
```bash
python3 -m venv .venv
source .venv/bin/activate
pip install kafka-python
python3 scripts/producer_simulator.py
```

The script uses the default Kafka bootstrap server `localhost:9092`. If your Kafka broker is running elsewhere, edit `scripts/producer_simulator.py` and update `KAFKA_BOOTSTRAP = "host:port"` accordingly. Stop the simulator with Ctrl+C.

## Notes

- Database schema and time-series tables are managed with Liquibase changelogs under `src/main/resources/db/changelog`.
- Kafka error handling and dead-letter configuration is implemented under `infrastructure/kafka`.
- The project uses separate adapters for snapshot storage (`persistence/postgres`) and time-series (`persistence/timescale`).

## License & Contact

This repository is provided as a demo. For questions or improvements, open an issue or contact the maintainer.