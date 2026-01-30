package com.twins.demo_twins.domain.event;

import java.time.Instant;

public record TemperatureSensorEvent(
        String assetId,
        String sensorId,
        double value,
        SensorType type,
        Instant eventTime
) implements SensorEvent {
    @Override
    public String getAssetId() {
        return assetId;
    }

    @Override
    public String getSensorId() {
        return sensorId;
    }

    @Override
    public Double getValue() {
        return value;
    }

    @Override
    public SensorType getType() {
        return type;
    }

    @Override
    public Instant getEventTime() {
        return eventTime;
    }
}
