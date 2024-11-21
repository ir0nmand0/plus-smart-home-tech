package ru.yandex.practicum.avro.mapper.sensor;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.model.sensor.SwitchSensorEvent;

@Mapper(componentModel = "spring")
public interface SwitchSensorPayloadMapper {
    @Named("mapToPayload")
    SwitchSensorAvro toAvro(SwitchSensorEvent event);
}
