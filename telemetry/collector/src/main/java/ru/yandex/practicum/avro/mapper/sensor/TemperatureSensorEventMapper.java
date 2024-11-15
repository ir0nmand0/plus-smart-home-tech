package ru.yandex.practicum.avro.mapper.sensor;

import org.mapstruct.Mapper;
import ru.yandex.practicum.avro.mapper.annotations.SensorEventMapping;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.model.sensor.TemperatureSensorEvent;

@Mapper(componentModel = "spring", uses = TemperatureSensorPayloadMapper.class)
public interface TemperatureSensorEventMapper {
    @SensorEventMapping
    SensorEventAvro toAvro(TemperatureSensorEvent event);
}