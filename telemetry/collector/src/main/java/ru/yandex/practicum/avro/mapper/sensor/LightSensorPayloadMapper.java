package ru.yandex.practicum.avro.mapper.sensor;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.model.sensor.LightSensorEvent;

@Mapper(componentModel = "spring")
public interface LightSensorPayloadMapper {
    @Named("mapToPayload")
    LightSensorAvro toAvro(LightSensorEvent event);
}
