package ru.yandex.practicum.avro.mapper.sensor;

import org.mapstruct.Mapper;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.model.sensor.ClimateSensorEvent;

@Mapper(componentModel = "spring")
public interface ClimateSensorPayloadMapper {
    ClimateSensorAvro toAvro(ClimateSensorEvent event);
}
