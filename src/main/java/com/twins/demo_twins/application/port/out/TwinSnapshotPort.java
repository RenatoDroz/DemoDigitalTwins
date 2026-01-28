package com.twins.demo_twins.application.port.out;

import com.twins.demo_twins.domain.dto.TwinChangeSet;
import com.twins.demo_twins.domain.dto.DrillBitSnapshotDTO;
import com.twins.demo_twins.domain.dto.SensorSnapshotDTO;
import com.twins.demo_twins.domain.twin.DrillBitTwin;

import java.util.List;

public interface TwinSnapshotPort {

    void maybePersist(DrillBitTwin twin, TwinChangeSet changeSet);

    DrillBitSnapshotDTO loadDrillBit(String assetId);

    List<SensorSnapshotDTO> loadSensors(String assetId);
}
