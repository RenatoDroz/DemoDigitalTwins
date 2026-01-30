package com.twins.demo_twins.infrastructure.kafka;

import com.twins.demo_twins.application.port.in.SensorEventUseCase;
import com.twins.demo_twins.domain.event.SensorEvent;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class SensorEventListener {

    private final SensorEventUseCase eventUseCase;
    private final MeterRegistry meterRegistry;


    @KafkaListener(
            topics = "sensor-events",
            properties = {
                    "spring.json.value.default.type=com.twins.demo_twins.domain.event.SensorEvent"
            }
    )
    public void eventListner(SensorEvent event) {

        Timer.Sample sample = Timer.start(meterRegistry);

        try {
            eventUseCase.handle(event);
        } finally {
            sample.stop(
                    Timer.builder("twin.event.processing.time")
                            .description("Time to process a sensor event")
                            .tag("type", event.getType().name())
                            .register(meterRegistry)
            );
        }
    }

}
