package ru.yandex.practicum.avro.mapper.sensor;

import org.mapstruct.Mapper;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;
import ru.yandex.practicum.model.sensor.TemperatureSensorEvent;

@Mapper(componentModel = "spring")
public interface TemperatureSensorPayloadMapper {
    TemperatureSensorAvro toAvro(TemperatureSensorEvent event);
}
