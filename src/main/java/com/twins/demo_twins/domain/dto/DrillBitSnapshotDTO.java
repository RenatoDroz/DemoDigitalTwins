package com.twins.demo_twins.domain.dto;

import com.twins.demo_twins.domain.twin.Status;

import java.time.Instant;

public record DrillBitSnapshotDTO(
        String assetId,
        Status status,
        long version,
        Instant lastUpdate
) {
}