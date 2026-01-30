package com.twins.demo_twins.infrastructure.persistence.postgres.repository;

import com.twins.demo_twins.infrastructure.persistence.postgres.entity.SensorEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.Instant;
import java.util.List;

public interface SensorRepository extends JpaRepository<SensorEntity, Long> {

    @Modifying
    @Query(value = """
            INSERT INTO sensor (
                asset_id, sensor_id, type, status,
                last_value, last_update, version
            )
            VALUES (
                :assetId, :sensorId, :type, :status,
                :lastValue, :lastUpdate, :version
            )
            ON CONFLICT (asset_id, sensor_id)
            DO UPDATE SET
                status = EXCLUDED.status,
                last_value = EXCLUDED.last_value,
                last_update = EXCLUDED.last_update,
                version = EXCLUDED.version
            WHERE sensor.version < EXCLUDED.version
            """, nativeQuery = true)
    void upsert(
            String assetId,
            String sensorId,
            String type,
            String status,
            Double lastValue,
            Instant lastUpdate,
            Long version
    );

    List<SensorEntity> findByAssetId(String assetId);
}

