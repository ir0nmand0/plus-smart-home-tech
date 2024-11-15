package ru.yandex.practicum.model.sensor;

public interface EventProcessorVisitor {
    void process(LightSensorEvent event);

    void process(TemperatureSensorEvent event);

    void process(SwitchSensorEvent event);

    void process(ClimateSensorEvent event);

    void process(MotionSensorEvent event);
}
