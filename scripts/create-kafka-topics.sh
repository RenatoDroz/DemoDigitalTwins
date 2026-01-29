#!/bin/bash

KAFKA_CONTAINER="kafka"
BROKER="localhost:9092"

docker exec -it $KAFKA_CONTAINER kafka-topics \
  --create \
  --if-not-exists \
  --topic sensor-events \
  --bootstrap-server $BROKER \
  --partitions 1 \
  --replication-factor 1

docker exec -it $KAFKA_CONTAINER kafka-topics \
  --create \
  --if-not-exists \
  --topic sensor-events-dlt \
  --bootstrap-server $BROKER \
  --partitions 1 \
  --replication-factor 1 \
  --config retention.ms=604800000 \
  --config cleanup.policy=delete

echo "Topics created."
