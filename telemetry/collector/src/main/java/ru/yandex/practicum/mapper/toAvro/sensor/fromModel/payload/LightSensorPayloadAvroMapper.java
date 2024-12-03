package ru.yandex.practicum.mapper.toAvro.sensor.fromModel.payload;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;
import ru.yandex.practicum.model.sensor.LightSensorEvent;

@Mapper(componentModel = "spring")
public interface LightSensorPayloadAvroMapper {
    @Named("mapToPayload")
    LightSensorAvro toAvro(LightSensorEvent event);
}
