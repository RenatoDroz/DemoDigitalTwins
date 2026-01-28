package com.twins.demo_twins.domain.dto;

import com.twins.demo_twins.domain.twin.SensorTwin;

public record TwinChangeSet(
        SensorTwin sensor,
        boolean newSensor,
        boolean sensorChanged,
        boolean drillBitChanged
) {

    public boolean shouldPersistSensor() {
        return newSensor || sensorChanged;
    }
}

