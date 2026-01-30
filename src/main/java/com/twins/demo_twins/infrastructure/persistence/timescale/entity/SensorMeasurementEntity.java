package com.twins.demo_twins.infrastructure.persistence.timescale.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "sensor_measurement")
@IdClass(SensorMeasurementId.class)
public class SensorMeasurementEntity {

    @Id
    private Instant time;

    @Id
    private String sensorId;

    private String assetId;

    private String type;

    private Double value;

    private String status;
}
