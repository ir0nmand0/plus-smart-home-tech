package ru.yandex.practicum.mapper.toAvro.hub.fromModel.payload;

import org.mapstruct.Mapper;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.model.hub.DeviceAction;

@Mapper(componentModel = "spring")
public interface DeviceActionAvroMapper {
    DeviceActionAvro toAvro(DeviceAction deviceAction);
}
