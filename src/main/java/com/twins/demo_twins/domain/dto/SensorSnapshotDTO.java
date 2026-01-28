package com.twins.demo_twins.domain.dto;

import com.twins.demo_twins.domain.event.SensorType;
import com.twins.demo_twins.domain.twin.Status;

import java.time.Instant;

public record SensorSnapshotDTO(
        String sensorId,
        String assetId,
        SensorType type,
        Status status,
        Double value,
        Instant lastUpdate
) {
}