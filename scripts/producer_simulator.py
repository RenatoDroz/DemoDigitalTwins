import json
import random
import time
from datetime import datetime, timezone
from kafka import KafkaProducer

KAFKA_BOOTSTRAP = "localhost:9092"
TOPIC = "sensor-events"

producer = KafkaProducer(
    bootstrap_servers=KAFKA_BOOTSTRAP,
    key_serializer=lambda k: k.encode(),
    value_serializer=lambda v: json.dumps(v).encode("utf-8")
)

assets = ["drill-bit-1", "drill-bit-2"]

sensors = [
    ("pressure-1", "PRESSURE"),
    ("pressure-2", "PRESSURE"),
    ("temperature-1", "TEMPERATURE"),
    ("temperature-2", "TEMPERATURE"),
]

def now_iso():
    return datetime.now(timezone.utc).isoformat()

def generate_value(sensor_type: str):
    if sensor_type == "PRESSURE":
        if random.random() < 0.9:
            return random.uniform(20, 80)   # OK (<100)
        else:
            return random.uniform(120, 180) # ALERT (>100)

    if sensor_type == "TEMPERATURE":
        if random.random() < 0.9:
            return random.uniform(10, 40)   # OK (<50)
        else:
            return random.uniform(60, 90)   # ALERT (>50)

    return 0.0

print("Starting sensor event simulator... Ctrl+C to stop.")

try:
    while True:
        for asset_id in assets:
            for sensor_id, sensor_type in sensors:

                value = round(generate_value(sensor_type), 2)

                event = {
                    "assetId": asset_id,
                    "sensorId": sensor_id,
                    "value": value,
                    "type": sensor_type,
                    "eventTime": now_iso()
                }

                key = asset_id

                producer.send(TOPIC, key=key, value=event)

                print(f"Sent -> {event}")

        producer.flush()
        time.sleep(0.5)

except KeyboardInterrupt:
    print("Stopping simulator...")
    producer.close()
