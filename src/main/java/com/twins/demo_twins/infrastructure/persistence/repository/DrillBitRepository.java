package com.twins.demo_twins.infrastructure.persistence.repository;

import com.twins.demo_twins.infrastructure.persistence.entity.DrillBitEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface DrillBitRepository extends JpaRepository<DrillBitEntity, String> {

    Optional<DrillBitEntity> findByAssetId(String assetId);

    @Modifying
    @Query(value = """
            INSERT INTO drill_bit (asset_id, status, last_update, version)
            VALUES (:assetId, :status, :lastUpdate, :version)
            ON CONFLICT (asset_id)
            DO UPDATE SET
                status = EXCLUDED.status,
                last_update = EXCLUDED.last_update,
                version = EXCLUDED.version
            WHERE drill_bit.version < EXCLUDED.version
            """, nativeQuery = true)
    void upsert(
            @Param("assetId") String assetId,
            @Param("status") String status,
            @Param("lastUpdate") Instant lastUpdate,
            @Param("version") Long version
    );
}