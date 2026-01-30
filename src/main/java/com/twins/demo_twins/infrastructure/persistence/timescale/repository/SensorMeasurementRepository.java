package com.twins.demo_twins.infrastructure.persistence.timescale.repository;

import com.twins.demo_twins.infrastructure.persistence.timescale.entity.SensorMeasurementEntity;
import com.twins.demo_twins.infrastructure.persistence.timescale.entity.SensorMeasurementId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SensorMeasurementRepository extends JpaRepository<SensorMeasurementEntity, SensorMeasurementId> {
}
