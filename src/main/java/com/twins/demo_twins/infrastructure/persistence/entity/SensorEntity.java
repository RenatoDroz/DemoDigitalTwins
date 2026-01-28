package com.twins.demo_twins.infrastructure.persistence.entity;

import com.twins.demo_twins.domain.event.SensorType;
import com.twins.demo_twins.domain.twin.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "sensor")
public class SensorEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sensor_id", nullable = false)
    private String sensorId;

    @Column(name = "asset_id", nullable = false)
    private String assetId;

    @Enumerated(EnumType.STRING)
    private SensorType type;

    private Double lastValue;

    @Enumerated(EnumType.STRING)
    private Status status;

    @UpdateTimestamp
    private Instant lastUpdate;

    @Version
    private Long version;

}