package com.twins.demo_twins.application.port.out;

import com.twins.demo_twins.domain.dto.DrillBitSnapshotDTO;
import com.twins.demo_twins.domain.dto.SensorSnapshotDTO;
import com.twins.demo_twins.domain.twin.DrillBitTwin;

import java.util.List;
import java.util.Optional;

public interface TwinStateStorePort {

    Optional<DrillBitSnapshotDTO> loadDrillBit(String assetId);

    List<SensorSnapshotDTO> loadSensors(String assetId);

    void save(DrillBitTwin twin);

    void evict(String assetId);
}