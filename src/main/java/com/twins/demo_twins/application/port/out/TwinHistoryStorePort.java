package com.twins.demo_twins.application.port.out;

import com.twins.demo_twins.domain.twin.DrillBitTwin;
import com.twins.demo_twins.domain.twin.SensorTwin;

public interface TwinHistoryStorePort {

    void saveHistoryPoint(DrillBitTwin drillBit, SensorTwin sensor);
}
