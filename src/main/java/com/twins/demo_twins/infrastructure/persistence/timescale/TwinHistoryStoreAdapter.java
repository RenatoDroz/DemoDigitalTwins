package com.twins.demo_twins.infrastructure.persistence.timescale;

import com.twins.demo_twins.application.port.out.TwinHistoryStorePort;
import com.twins.demo_twins.domain.twin.DrillBitTwin;
import com.twins.demo_twins.domain.twin.SensorTwin;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TwinHistoryStoreAdapter implements TwinHistoryStorePort {

    private final TimeSeriesService service;

    @Override
    public void saveHistoryPoint(DrillBitTwin drillBit, SensorTwin sensor) {
        service.savePoint(drillBit, sensor);
    }
}
