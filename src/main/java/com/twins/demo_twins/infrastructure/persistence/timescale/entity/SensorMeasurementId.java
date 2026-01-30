package com.twins.demo_twins.infrastructure.persistence.timescale.entity;

import java.io.Serializable;
import java.time.Instant;

public class SensorMeasurementId implements Serializable {
    private Instant time;
    private String sensorId;
}
