package com.twins.demo_twins.infrastructure.persistence.timescale;

import com.twins.demo_twins.domain.twin.DrillBitTwin;
import com.twins.demo_twins.domain.twin.SensorTwin;
import com.twins.demo_twins.infrastructure.persistence.timescale.entity.SensorMeasurementEntity;
import com.twins.demo_twins.infrastructure.persistence.timescale.repository.SensorMeasurementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TimeSeriesService {

    private final SensorMeasurementRepository repository;

    public void savePoint(DrillBitTwin drillBit, SensorTwin sensor) {
        SensorMeasurementEntity entity = new SensorMeasurementEntity();

        entity.setTime(sensor.getLastUpdate());
        entity.setAssetId(drillBit.getAssetId());
        entity.setSensorId(sensor.getSensorId());
        entity.setType(sensor.getType().name());
        entity.setValue(sensor.getValue());
        entity.setStatus(sensor.getStatus().name());

        repository.save(entity);
    }
}
