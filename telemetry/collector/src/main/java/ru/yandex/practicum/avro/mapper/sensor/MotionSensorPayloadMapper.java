package ru.yandex.practicum.avro.mapper.sensor;


import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.model.sensor.MotionSensorEvent;

public interface MotionSensorPayloadMapper {
    MotionSensorAvro toAvro(MotionSensorEvent event);
}
