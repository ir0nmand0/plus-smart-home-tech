package ru.yandex.practicum.avro.mapper.sensor;

import org.mapstruct.Mapper;
import ru.yandex.practicum.avro.mapper.annotations.SensorEventMapping;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.model.sensor.ClimateSensorEvent;

@Mapper(componentModel = "spring", uses = ClimateSensorPayloadMapper.class)
public interface ClimateSensorEventMapper {
    @SensorEventMapping
    SensorEventAvro toAvro(ClimateSensorEvent event);
}
