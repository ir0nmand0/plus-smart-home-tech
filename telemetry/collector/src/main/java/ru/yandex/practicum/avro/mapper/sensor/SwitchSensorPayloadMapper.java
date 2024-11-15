package ru.yandex.practicum.avro.mapper.sensor;

import org.mapstruct.Mapper;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;
import ru.yandex.practicum.model.sensor.SwitchSensorEvent;

@Mapper(componentModel = "spring")
public interface SwitchSensorPayloadMapper {
    SwitchSensorAvro toAvro(SwitchSensorEvent event);
}
