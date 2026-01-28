package com.twins.demo_twins.application.port.in;

import com.twins.demo_twins.domain.event.SensorEvent;

public interface SensorEventUseCase {

    void handle(SensorEvent event);
}
