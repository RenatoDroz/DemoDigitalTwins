package com.twins.demo_twins.application.runtime;

import com.twins.demo_twins.application.port.in.SensorEventUseCase;
import com.twins.demo_twins.application.port.out.TwinSnapshotPort;
import com.twins.demo_twins.application.port.out.TwinStateStorePort;
import com.twins.demo_twins.domain.dto.DrillBitSnapshotDTO;
import com.twins.demo_twins.domain.dto.SensorSnapshotDTO;
import com.twins.demo_twins.domain.dto.TwinChangeSet;
import com.twins.demo_twins.domain.event.SensorEvent;
import com.twins.demo_twins.domain.twin.DrillBitTwin;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class DrillBitTwinRuntime implements SensorEventUseCase {

    private final TwinSnapshotPort twinSnapshotPort;
    private final TwinStateStorePort twinStateStorePort;

    private final Map<String, DrillBitTwin> twins = new ConcurrentHashMap<>();

    public void handle(SensorEvent event) {

        DrillBitTwin drillBit = twins.computeIfAbsent(
                event.getAssetId(),
                this::createOrRestoreTwin
        );

        TwinChangeSet changeSet = drillBit.apply(event);

        twinStateStorePort.save(drillBit);
        twinSnapshotPort.maybePersist(drillBit, changeSet);
    }

    public DrillBitTwin createOrRestoreTwin(String assetId) {

        DrillBitTwin drillBitTwin = new DrillBitTwin(assetId);

        Optional<DrillBitSnapshotDTO> redisDrillBit = twinStateStorePort.loadDrillBit(assetId);

        if (redisDrillBit.isPresent()) {
            drillBitTwin.restore(redisDrillBit.get(), twinStateStorePort.loadSensors(assetId));
            log.info("DrillBit Twin restored for asset {}", assetId);
            return drillBitTwin;
        }

        var drillBit = twinSnapshotPort.loadDrillBit(assetId);

        if (drillBit != null) {
            List<SensorSnapshotDTO> sensors = twinSnapshotPort.loadSensors(assetId);
            drillBitTwin.restore(drillBit, sensors);
            log.info("DrillBit Twin restored for asset {}", assetId);
        }
        log.info("DrillBit Twin created for asset {}", assetId);
        return drillBitTwin;
    }
}
