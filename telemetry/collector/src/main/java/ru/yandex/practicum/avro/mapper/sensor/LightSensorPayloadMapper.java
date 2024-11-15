package ru.yandex.practicum.avro.mapper.sensor;

import org.mapstruct.Mapper;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.model.sensor.LightSensorEvent;

@Mapper(componentModel = "spring")
public interface LightSensorPayloadMapper {
    LightSensorAvro toAvro(LightSensorEvent event);
}
