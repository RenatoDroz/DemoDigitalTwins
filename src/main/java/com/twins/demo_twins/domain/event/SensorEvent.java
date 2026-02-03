package com.twins.demo_twins.domain.event;

import java.time.Instant;

public interface SensorEvent {

    String getAssetId();

    String getSensorId();

    Double getValue();

    SensorType getType();

    Instant getEventTime();
}

