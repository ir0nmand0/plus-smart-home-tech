package ru.yandex.practicum.mapper.toAvro.sensor.fromModel.payload;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.model.sensor.SwitchSensorEvent;

@Mapper(componentModel = "spring")
public interface SwitchSensorPayloadAvroMapper {
    @Named("mapToPayload")
    SwitchSensorAvro toAvro(SwitchSensorEvent event);
}
