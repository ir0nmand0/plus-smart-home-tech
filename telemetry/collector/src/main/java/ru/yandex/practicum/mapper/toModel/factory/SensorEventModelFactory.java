package ru.yandex.practicum.mapper.toModel.factory;

import org.springframework.stereotype.Component;
import ru.yandex.practicum.model.sensor.*;

@Component
public class SensorEventModelFactory {

    public MotionSensorEvent createMotionSensorEvent() {
        return new MotionSensorEvent();
    }

    public TemperatureSensorEvent createTemperatureSensorEvent() {
        return new TemperatureSensorEvent();
    }

    public LightSensorEvent createLightSensorEvent() {
        return new LightSensorEvent();
    }

    public ClimateSensorEvent createClimateSensorEvent() {
        return new ClimateSensorEvent();
    }

    public SwitchSensorEvent createSwitchSensorEvent() {
        return new SwitchSensorEvent();
    }
}
