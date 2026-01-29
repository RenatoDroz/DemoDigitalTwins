package com.twins.demo_twins.domain.twin;

import com.twins.demo_twins.domain.dto.DrillBitSnapshotDTO;
import com.twins.demo_twins.domain.dto.SensorSnapshotDTO;
import com.twins.demo_twins.domain.dto.TwinChangeSet;
import com.twins.demo_twins.domain.event.SensorEvent;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.twins.demo_twins.domain.twin.Status.ALERT;
import static com.twins.demo_twins.domain.twin.Status.OK;

@Slf4j
@Getter
@Setter
public class DrillBitTwin {

    private Long id;
    private final String assetId;
    private Status status = OK;
    private Instant lastUpdate;
    private Long version = 0L;
    private final Map<String, SensorTwin> sensors = new HashMap<>();

    public DrillBitTwin(String assetId) {
        this.assetId = assetId;
    }

    public TwinChangeSet apply(SensorEvent event) {

        boolean isNewSensor = !sensors.containsKey(event.getSensorId());

        SensorTwin sensor = sensors.computeIfAbsent(
                event.getSensorId(),
                id -> createSensorTwin(event)
        );

        boolean sensorChanged = sensor.apply(event);

        Status previousStatus = this.status;
        recalculateStatus();


        boolean drillBitChanged = isNewSensor || sensorChanged || previousStatus != status;

        if (drillBitChanged) {
            version++;
            this.lastUpdate = event.getEventTime();
        }

        return new TwinChangeSet(sensor, isNewSensor, sensorChanged, drillBitChanged);
    }

    private SensorTwin createSensorTwin(SensorEvent event) {
        log.info("Creating {} Sensor Twin for sensorId: {}", event.getType().name(), event.getSensorId());
        return switch (event.getType()) {
            case TEMPERATURE -> new TemperatureSensorTwin(event.getSensorId());
            case PRESSURE -> new PressureSensorTwin(event.getSensorId());
        };
    }

    public void restore(
            DrillBitSnapshotDTO drillBit,
            List<SensorSnapshotDTO> sensorEntityList
    ) {
        this.status = drillBit.status();
        this.version = drillBit.version();
        this.lastUpdate = drillBit.lastUpdate();

        for (SensorSnapshotDTO sensor : sensorEntityList) {
            SensorTwin sensorTwin = restoreSensorTwin(sensor);
            sensors.put(sensor.sensorId(), sensorTwin);
        }
    }

    private SensorTwin restoreSensorTwin(SensorSnapshotDTO sensorDTO) {
        log.info("Restoring Sensor Twin for sensorId: {}", sensorDTO.sensorId());
        return switch (sensorDTO.type()) {
            case TEMPERATURE -> {
                TemperatureSensorTwin t = new TemperatureSensorTwin(sensorDTO.sensorId());
                t.restore(sensorDTO.status(), sensorDTO.value(), sensorDTO.lastUpdate());
                yield t;
            }
            case PRESSURE -> {
                PressureSensorTwin p = new PressureSensorTwin(sensorDTO.sensorId());
                p.restore(sensorDTO.status(), sensorDTO.value(), sensorDTO.lastUpdate());
                yield p;
            }
        };
    }

    private void recalculateStatus() {
        var currentStatus = sensors.values().stream()
                .anyMatch(s -> s.status == ALERT)
                ? ALERT
                : OK;

        if (status != currentStatus) {
            log.info("DrillBit {} status changed -  from: {} to : {}", assetId, status, currentStatus);
            status = currentStatus;
        }
    }
}
