package com.twins.demo_twins.domain.twin;

import com.twins.demo_twins.domain.event.SensorEvent;
import com.twins.demo_twins.domain.event.SensorType;
import lombok.Getter;

import java.time.Instant;

@Getter
public abstract class SensorTwin {

    protected String sensorId;
    protected Status status;
    protected Double value;
    protected Instant lastUpdate;

    protected SensorTwin(String sensorId) {
        this.sensorId = sensorId;
        this.status = Status.OK;
    }

    public abstract boolean apply(SensorEvent event);

    public abstract SensorType getType();

    public abstract Double getValue();

    public abstract void restoreValue(Double value);

    public void restore(Status status, Double value, Instant lastUpdate) {
        this.status = status;
        this.lastUpdate = lastUpdate;
        restoreValue(value);
    }

}

