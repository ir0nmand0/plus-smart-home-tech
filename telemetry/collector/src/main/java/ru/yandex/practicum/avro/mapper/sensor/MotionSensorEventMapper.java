package ru.yandex.practicum.avro.mapper.sensor;

import org.mapstruct.Mapper;
import ru.yandex.practicum.avro.mapper.annotations.SensorEventMapping;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.model.sensor.MotionSensorEvent;

@Mapper(componentModel = "spring", uses = MotionSensorPayloadMapper.class)
public interface MotionSensorEventMapper {
    @SensorEventMapping
    SensorEventAvro toAvro(MotionSensorEvent event);
}
