package ru.yandex.practicum.avro.mapper.sensor;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;
import ru.yandex.practicum.model.sensor.TemperatureSensorEvent;

@Mapper(componentModel = "spring")
public interface TemperatureSensorPayloadMapper {
    @Named("mapToPayload")
    TemperatureSensorAvro toAvro(TemperatureSensorEvent event);
}
