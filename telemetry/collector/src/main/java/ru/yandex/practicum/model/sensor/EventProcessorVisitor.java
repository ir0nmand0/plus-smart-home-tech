package ru.yandex.practicum.model.sensor;

public interface EventProcessorVisitor {
    void visit(LightSensorEvent event);

    void visit(TemperatureSensorEvent event);

    void visit(SwitchSensorEvent event);

    void visit(ClimateSensorEvent event);

    void visit(MotionSensorEvent event);
}
