package com.twins.demo_twins.infrastructure.persistence.postgres;

import com.twins.demo_twins.application.port.out.TwinSnapshotStorePort;
import com.twins.demo_twins.domain.dto.DrillBitSnapshotDTO;
import com.twins.demo_twins.domain.dto.SensorSnapshotDTO;
import com.twins.demo_twins.domain.dto.TwinChangeSet;
import com.twins.demo_twins.domain.twin.DrillBitTwin;
import com.twins.demo_twins.infrastructure.persistence.postgres.repository.DrillBitRepository;
import com.twins.demo_twins.infrastructure.persistence.postgres.repository.SensorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TwinSnapshotStoreAdapter implements TwinSnapshotStorePort {

    private final DrillBitRepository drillBitRepository;
    private final SensorRepository sensorRepository;
    private final TwinSnapshotWriteService persistenceService;


    @Override
    public void maybePersist(DrillBitTwin twin, TwinChangeSet changeSet) {
        persistenceService.maybePersist(twin, changeSet);
    }

    @Override
    public DrillBitSnapshotDTO loadDrillBit(String assetId) {
        return drillBitRepository.findByAssetId(assetId)
                .map(entity -> new DrillBitSnapshotDTO(
                        entity.getAssetId(),
                        entity.getStatus(),
                        entity.getVersion(),
                        entity.getLastUpdate()
                ))
                .orElse(null);
    }

    @Override
    public List<SensorSnapshotDTO> loadSensors(String assetId) {
        return sensorRepository.findByAssetId(assetId).stream()
                .map(entity -> new SensorSnapshotDTO(
                        entity.getSensorId(),
                        entity.getAssetId(),
                        entity.getType(),
                        entity.getStatus(),
                        entity.getLastValue(),
                        entity.getLastUpdate()
                )).toList();
    }
}
