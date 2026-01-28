package com.twins.demo_twins.domain.twin;

import com.twins.demo_twins.domain.event.SensorEvent;
import com.twins.demo_twins.domain.event.SensorType;
import com.twins.demo_twins.domain.event.TemperatureSensorEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
@Setter
public class TemperatureSensorTwin extends SensorTwin {

    public TemperatureSensorTwin(String id) {
        super(id);
        log.info("Temperature Twin created for sensor {}", id);
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
        return SensorType.TEMPERATURE;
    }

    @Override
    public boolean apply(SensorEvent event) {
        TemperatureSensorEvent e = (TemperatureSensorEvent) event;


        Status previousStatus = this.status;
        double previousTemperatureValue = this.value;

        this.value = e.value();
        this.lastUpdate = event.getEventTime();
        this.status = value > 50 ? Status.ALERT : Status.OK;

        boolean changed = previousTemperatureValue != value || previousStatus != status;

        if (previousStatus != status) {
            log.warn("Temperature sensor {} status changed: {} -> {}",
                    sensorId, previousStatus, status);
        }

        return changed;
    }
}
