package com.twins.demo_twins.infrastructure.redis;

import com.twins.demo_twins.domain.event.SensorType;
import com.twins.demo_twins.domain.twin.Status;

import java.time.Instant;

public class SensorAccumulator {
    String sensorId;
    SensorType type;
    Status status;
    Double value;
    Instant lastUpdate;
}