package com.twins.demo_twins.domain.twin;

import com.twins.demo_twins.domain.event.PressureSensorEvent;
import com.twins.demo_twins.domain.event.SensorEvent;
import com.twins.demo_twins.domain.event.SensorType;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
public class PressureSensorTwin extends SensorTwin {

    public PressureSensorTwin(String id) {
        super(id);
        log.info("Pressure Twin created for sensor {}", id);
    }

    @Override
    public Double getValue() {
        return value;
    }

    @Override
    public void restoreValue(Double value) {
        this.value = value != null ? value : 0.0;
    }

    @Override
    public SensorType getType() {
        return SensorType.PRESSURE;
    }

    @Override
    public boolean apply(SensorEvent event) {

        PressureSensorEvent e = (PressureSensorEvent) event;

        Status previousStatus = this.status;
        double previousPressureValue = this.value;

        this.value = e.value();
        this.lastUpdate = event.getEventTime();
        this.status = value > 100 ? Status.ALERT : Status.OK;

        boolean changed = previousStatus != status || previousPressureValue != value;

        if (previousStatus != status) {
            log.warn("Pressure sensor {} status changed: {} -> {}",
                    sensorId, previousStatus, status);
        }

        return changed;
    }

}
