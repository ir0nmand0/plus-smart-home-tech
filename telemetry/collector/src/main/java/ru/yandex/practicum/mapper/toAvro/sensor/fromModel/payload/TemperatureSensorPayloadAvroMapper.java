package ru.yandex.practicum.mapper.toAvro.sensor.fromModel.payload;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;
import ru.yandex.practicum.model.sensor.TemperatureSensorEvent;

@Mapper(componentModel = "spring")
public interface TemperatureSensorPayloadAvroMapper {
    @Named("mapToPayload")
    TemperatureSensorAvro toAvro(TemperatureSensorEvent event);
}
