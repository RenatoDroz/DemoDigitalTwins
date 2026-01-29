package com.twins.demo_twins.infrastructure.redis;

import com.twins.demo_twins.domain.event.SensorType;
import com.twins.demo_twins.domain.twin.SensorTwin;
import com.twins.demo_twins.domain.twin.Status;

import java.time.Instant;
import java.util.function.BiConsumer;
import java.util.function.Function;

enum SensorField {

    TYPE(
            "type",
            (a, v) -> a.type = SensorType.valueOf(v),
            s -> s.getType().name()
    ),

    STATUS(
            "status",
            (a, v) -> a.status = Status.valueOf(v),
            s -> s.getStatus().name()
    ),

    VALUE(
            "value",
            (a, v) -> a.value = Double.valueOf(v),
            s -> String.valueOf(s.getValue())
    ),

    LAST_UPDATE(
            "lastUpdate",
            (a, v) -> a.lastUpdate = Instant.parse(v),
            s -> s.getLastUpdate().toString()
    );

    final String redisName;
    final BiConsumer<SensorAccumulator, String> reader;
    final Function<SensorTwin, String> writer;

    SensorField(
            String redisName,
            BiConsumer<SensorAccumulator, String> reader,
            Function<SensorTwin, String> writer
    ) {
        this.redisName = redisName;
        this.reader = reader;
        this.writer = writer;
    }

    static SensorField fromRedis(String redisName) {
        for (SensorField f : values()) {
            if (f.redisName.equals(redisName)) {
                return f;
            }
        }
        return null;
    }

    void read(SensorAccumulator acc, String value) {
        reader.accept(acc, value);
    }

    String write(SensorTwin sensor) {
        return writer.apply(sensor);
    }
}
