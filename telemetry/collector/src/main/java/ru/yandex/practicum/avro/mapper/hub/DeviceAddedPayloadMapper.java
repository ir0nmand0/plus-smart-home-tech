package ru.yandex.practicum.avro.mapper.hub;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.model.hub.DeviceAddedEvent;

@Mapper(componentModel = "spring", uses = DeviceTypeMapper.class)
public interface DeviceAddedPayloadMapper {
    @Mapping(target = "type", source = "deviceType")
    DeviceAddedEventAvro toAvro(DeviceAddedEvent event);
}
