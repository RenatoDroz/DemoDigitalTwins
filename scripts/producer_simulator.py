import os
import random
import time
import uuid
from datetime import datetime, timezone

from confluent_kafka import SerializingProducer
from confluent_kafka.schema_registry import SchemaRegistryClient
from confluent_kafka.schema_registry.avro import AvroSerializer

# ---------- PATH DO SCHEMA ----------

BASE_DIR = os.path.dirname(os.path.abspath(__file__))

AVRO_DIR = os.path.join(BASE_DIR, "..", "src", "main", "avro")

ENVELOPE_PATH = os.path.join(AVRO_DIR, "EventEnvelopeAvro.avsc")
SENSOR_PATH = os.path.join(AVRO_DIR, "SensorEventAvro.avsc")

# ---------- CONFIG ----------

KAFKA_BOOTSTRAP = "localhost:9092"
SCHEMA_REGISTRY = "http://127.0.0.1:8081"
TOPIC = "sensor-events"

# ---------- LOAD SCHEMAS ----------

from confluent_kafka.schema_registry import Schema, SchemaReference
from confluent_kafka.schema_registry.error import SchemaRegistryError

with open(SENSOR_PATH) as f:
    sensor_schema = f.read()

with open(ENVELOPE_PATH) as f:
    envelope_schema = f.read()

schema_registry_client = SchemaRegistryClient({"url": SCHEMA_REGISTRY})

# Ensure the sensor schema is registered and obtain its latest version
sensor_subject = "sensor-event-value"
try:
    latest = schema_registry_client.get_latest_version(sensor_subject)
    sensor_version = latest.version
except Exception:
    # Subject missing; register sensor schema
    sensor_schema_obj = Schema(sensor_schema, "AVRO")
    registered = schema_registry_client.register_schema(sensor_subject, sensor_schema_obj)
    sensor_version = registered.version

# Create reference to the sensor type for the envelope schema
sensor_ref = SchemaReference(
    name="com.twins.demo_twins.infrastructure.kafka.avro.SensorEventAvro",
    subject=sensor_subject,
    version=sensor_version,
)

# Build a Schema instance for the envelope including the reference
envelope_schema_obj = Schema(envelope_schema, "AVRO", references=[sensor_ref])

# Create Avro serializer with the Schema object (handles references)
avro_serializer = AvroSerializer(schema_registry_client, envelope_schema_obj)

producer = SerializingProducer({
    "bootstrap.servers": KAFKA_BOOTSTRAP,
    "key.serializer": lambda k, ctx: k.encode(),
    "value.serializer": avro_serializer
})

# ---------- SIMULADOR ----------

assets = ["drill-bit-1", "drill-bit-2"]

sensors = [
    ("pressure-1", "PRESSURE"),
    ("pressure-2", "PRESSURE"),
    ("temperature-1", "TEMPERATURE"),
    ("temperature-2", "TEMPERATURE"),
]

# Run single batch if RUN_ONCE environment variable is set to '1' (useful for testing)
RUN_ONCE = os.environ.get("RUN_ONCE") == "1"

def now_ms():
    return int(datetime.now(timezone.utc).timestamp() * 1000)

def generate_value(sensor_type: str):
    if sensor_type == "PRESSURE":
        if random.random() < 0.9:
            return random.uniform(20, 80)
        else:
            return random.uniform(120, 180)

    if sensor_type == "TEMPERATURE":
        if random.random() < 0.9:
            return random.uniform(10, 40)
        else:
            return random.uniform(60, 90)

    return 0.0

print("Starting sensor event simulator... Ctrl+C to stop.")

try:
    while True:
        for asset_id in assets:
            for sensor_id, sensor_type in sensors:

                payload = {
                    "assetId": asset_id,
                    "sensorId": sensor_id,
                    "value": round(generate_value(sensor_type), 2),
                    "type": sensor_type,
                    "eventTime": now_ms()
                }

                envelope = {
                    "eventId": str(uuid.uuid4()),
                    "eventType": "SensorEvent",
                    "source": "producer_simulator",
                    "timestamp": now_ms(),
                    "payload": payload
                }

                try:
                    producer.produce(
                        topic=TOPIC,
                        key=asset_id,
                        value=envelope
                    )
                    print(f"Sent -> {envelope}")
                except Exception as e:
                    print(f"Failed to produce message: {e}")

        producer.flush()

        if RUN_ONCE:
            break

        time.sleep(0.5)

except KeyboardInterrupt:
    print("Stopping simulator...")
finally:
    producer.flush()
    producer.close()
