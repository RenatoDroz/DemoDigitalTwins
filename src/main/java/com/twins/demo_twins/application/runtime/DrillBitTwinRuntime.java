package com.twins.demo_twins.application.runtime;

import com.twins.demo_twins.domain.dto.TwinChangeSet;
import com.twins.demo_twins.application.port.in.SensorEventUseCase;
import com.twins.demo_twins.application.port.out.TwinSnapshotPort;
import com.twins.demo_twins.domain.dto.SensorSnapshotDTO;
import com.twins.demo_twins.domain.event.SensorEvent;
import com.twins.demo_twins.domain.twin.DrillBitTwin;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class DrillBitTwinRuntime implements SensorEventUseCase {

    private final TwinSnapshotPort twinSnapshotPort;
    private final Map<String, DrillBitTwin> twins = new ConcurrentHashMap<>();

    public void handle(SensorEvent event) {

        DrillBitTwin drillBit = twins.computeIfAbsent(
                event.getAssetId(),
                this::createOrRestoreTwin
        );

        TwinChangeSet changeSet = drillBit.apply(event);

        twinSnapshotPort.maybePersist(drillBit, changeSet);
    }

    public DrillBitTwin createOrRestoreTwin(String assetId) {

        DrillBitTwin twin = new DrillBitTwin(assetId);
        var drillBit = twinSnapshotPort.loadDrillBit(assetId);

        if (drillBit != null) {
            List<SensorSnapshotDTO> sensors = twinSnapshotPort.loadSensors(assetId);
            twin.restore(drillBit, sensors);
        }

        return twin;
    }
}
