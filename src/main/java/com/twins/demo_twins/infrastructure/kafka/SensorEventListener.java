package com.twins.demo_twins.infrastructure.kafka;

import com.twins.demo_twins.application.port.in.SensorEventUseCase;
import com.twins.demo_twins.domain.event.SensorEvent;
import lombok.AllArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class SensorEventListener {

    private final SensorEventUseCase eventUseCase;


    @KafkaListener(
            topics = "sensor-events",
            properties = {
                    "spring.json.value.default.type=com.twins.demo_twins.domain.event.SensorEvent"
            }
    )
    public void eventListner(SensorEvent event) {
        eventUseCase.handle(event);
    }

}
