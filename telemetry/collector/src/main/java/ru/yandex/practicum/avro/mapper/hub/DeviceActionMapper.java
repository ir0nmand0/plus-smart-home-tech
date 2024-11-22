package ru.yandex.practicum.avro.mapper.hub;

import org.mapstruct.Mapper;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.model.hub.DeviceAction;

@Mapper(componentModel = "spring")
public interface DeviceActionMapper {
    DeviceActionAvro toAvro(DeviceAction deviceAction);
}
