package ru.yandex.practicum.mapper.toAvro.sensor.fromModel.payload;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;
import ru.yandex.practicum.model.sensor.ClimateSensorEvent;

@Mapper(componentModel = "spring")
public interface ClimateSensorPayloadAvroMapper {
    @Named("mapToPayload")
    ClimateSensorAvro toAvro(ClimateSensorEvent event);
}
