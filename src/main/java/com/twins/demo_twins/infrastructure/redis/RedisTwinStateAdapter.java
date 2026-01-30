package com.twins.demo_twins.infrastructure.redis;

import com.twins.demo_twins.application.port.out.TwinStateStorePort;
import com.twins.demo_twins.domain.dto.DrillBitSnapshotDTO;
import com.twins.demo_twins.domain.dto.SensorSnapshotDTO;
import com.twins.demo_twins.domain.twin.DrillBitTwin;
import com.twins.demo_twins.domain.twin.Status;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class RedisTwinStateAdapter implements TwinStateStorePort {

    @Value("${twins.redis.ttl}")
    private Duration ttl;
    private final RedisTemplate<String, String> redisTemplate;

    private static final String PREFIX = "twin:";

    @Override
    public Optional<DrillBitSnapshotDTO> loadDrillBit(String assetId) {

        String key = PREFIX + assetId;
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);

        if (entries.isEmpty()) {
            return Optional.empty();
        }

        DrillBitSnapshotDTO snapshot = new DrillBitSnapshotDTO(
                assetId,
                Status.valueOf((String) entries.get("status")),
                Long.parseLong((String) entries.get("version")),
                Instant.parse((String) entries.get("lastUpdate"))
        );

        return Optional.of(snapshot);
    }

    @Override
    public List<SensorSnapshotDTO> loadSensors(String assetId) {
        String key = PREFIX + assetId;
        Map<Object, Object> entries = redisTemplate.opsForHash().entries(key);

        Map<String, SensorAccumulator> sensors = new HashMap<>();

        for (Map.Entry<Object, Object> entry : entries.entrySet()) {
            String field = (String) entry.getKey();
            String value = (String) entry.getValue();

            if (!field.startsWith("sensor:")) continue;

            String[] parts = field.split(":");
            String sensorId = parts[1];
            String attr = parts[2];

            SensorAccumulator accumulator = sensors.computeIfAbsent(sensorId, id -> {
                SensorAccumulator sensorAccumulator = new SensorAccumulator();
                sensorAccumulator.sensorId = id;
                return sensorAccumulator;
            });

            SensorField sensorField = SensorField.fromRedis(attr);
            if (sensorField != null) {
                sensorField.read(accumulator, value);
            }
        }

        return sensors.values().stream().map(a -> new SensorSnapshotDTO(
                a.sensorId,
                assetId,
                a.type,
                a.status,
                a.value,
                a.lastUpdate
        )).toList();
    }

    @Override
    public void save(DrillBitTwin twin) {
        final String key = PREFIX + twin.getAssetId();
        final String SENSOR_PREFIX = "sensor:";

        HashOperations<String, String, String> ops = redisTemplate.opsForHash();
        Map<String, String> values = new HashMap<>();

        values.put("status", twin.getStatus().name());
        values.put("version", String.valueOf(twin.getVersion()));
        values.put("lastUpdate", twin.getLastUpdate().toString());

        twin.getSensors().forEach((sensorId, sensor) -> {
            for (SensorField field : SensorField.values()) {
                values.put(SENSOR_PREFIX + sensorId + ":" + field.redisName, field.write(sensor));
            }
        });

        ops.putAll(key, values);
        redisTemplate.expire(key, ttl);
    }

    @Override
    public void evict(String assetId) {
        redisTemplate.delete(PREFIX + assetId);
    }
}
