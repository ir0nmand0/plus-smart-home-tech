package ru.yandex.practicum.avro.mapper.sensor;

import org.mapstruct.Mapper;
import ru.yandex.practicum.avro.mapper.annotations.SensorEventMapping;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.model.sensor.LightSensorEvent;

@Mapper(componentModel = "spring", uses = LightSensorPayloadMapper.class)
public interface LightSensorEventMapper {
    @SensorEventMapping
    SensorEventAvro toAvro(LightSensorEvent event);
}