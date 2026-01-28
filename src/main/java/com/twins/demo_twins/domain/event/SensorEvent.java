package com.twins.demo_twins.domain.event;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.time.Instant;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.EXISTING_PROPERTY,
        property = "type",
        visible = true
)
@JsonSubTypes({
        @JsonSubTypes.Type(value = TemperatureSensorEvent.class, name = "TEMPERATURE"),
        @JsonSubTypes.Type(value = PressureSensorEvent.class, name = "PRESSURE")
})
public interface SensorEvent {

    String getAssetId();

    String getSensorId();

    SensorType getType();

    Instant getEventTime();
}

