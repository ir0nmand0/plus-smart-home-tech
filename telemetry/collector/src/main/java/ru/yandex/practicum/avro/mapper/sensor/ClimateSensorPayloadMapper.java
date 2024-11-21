package ru.yandex.practicum.avro.mapper.sensor;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.model.sensor.ClimateSensorEvent;

@Mapper(componentModel = "spring")
public interface ClimateSensorPayloadMapper {
    @Named("mapToPayload")
    ClimateSensorAvro toAvro(ClimateSensorEvent event);
}
