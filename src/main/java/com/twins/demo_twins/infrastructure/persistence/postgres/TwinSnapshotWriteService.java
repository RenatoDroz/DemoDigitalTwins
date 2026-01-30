package com.twins.demo_twins.infrastructure.persistence.postgres;

import com.twins.demo_twins.domain.dto.TwinChangeSet;
import com.twins.demo_twins.domain.twin.DrillBitTwin;
import com.twins.demo_twins.domain.twin.SensorTwin;
import com.twins.demo_twins.infrastructure.persistence.postgres.repository.DrillBitRepository;
import com.twins.demo_twins.infrastructure.persistence.postgres.repository.SensorRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@Slf4j
@RequiredArgsConstructor
public class TwinSnapshotWriteService {

    private static final Duration DEBOUNCE = Duration.ofMinutes(1);

    private final DrillBitRepository drillBitRepository;
    private final SensorRepository sensorRepository;

    private final Map<String, Instant> lastFlush = new ConcurrentHashMap<>();


    @Transactional
    public void maybePersist(DrillBitTwin twin, TwinChangeSet changeSet) {

        Instant now = Instant.now();
        Instant last = lastFlush.get(twin.getAssetId());

        boolean first = last == null;
        boolean debounceExpired = last != null &&
                Duration.between(last, now).compareTo(DEBOUNCE) >= 0;

        boolean shouldPersist = first || changeSet.drillBitChanged() || debounceExpired;

        if (!shouldPersist) {
            return;
        }

        drillBitRepository.upsert(
                twin.getAssetId(),
                twin.getStatus().name(),
                twin.getLastUpdate(),
                twin.getVersion()
        );

        if (changeSet.shouldPersistSensor()) {
            SensorTwin sensorTwin = changeSet.sensor();

            sensorRepository.upsert(
                    twin.getAssetId(),
                    sensorTwin.getSensorId(),
                    sensorTwin.getType().name(),
                    sensorTwin.getStatus().name(),
                    sensorTwin.getValue(),
                    twin.getLastUpdate(),
                    twin.getVersion()
            );
        }

        lastFlush.put(twin.getAssetId(), now);
    }
}
