package com.twins.demo_twins.infrastructure.kafka.mapper;

import com.twins.demo_twins.domain.event.PressureSensorEvent;
import com.twins.demo_twins.domain.event.SensorEvent;
import com.twins.demo_twins.domain.event.SensorType;
import com.twins.demo_twins.domain.event.TemperatureSensorEvent;
import com.twins.demo_twins.infrastructure.kafka.avro.SensorEventAvro;
import com.twins.demo_twins.infrastructure.kafka.avro.SensorTypeAvro;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface SensorEventMapper {

    default String map(CharSequence value) {
        return value == null ? null : value.toString();
    }

    default SensorType map(SensorTypeAvro type) {
        return SensorType.valueOf(type.name());
    }

    TemperatureSensorEvent toTemperature(SensorEventAvro avro);

    PressureSensorEvent toPressure(SensorEventAvro avro);

    default SensorEvent toDomain(SensorEventAvro avro) {
        return switch (avro.getType()) {
            case TEMPERATURE -> toTemperature(avro);
            case PRESSURE -> toPressure(avro);
        };
    }
}

