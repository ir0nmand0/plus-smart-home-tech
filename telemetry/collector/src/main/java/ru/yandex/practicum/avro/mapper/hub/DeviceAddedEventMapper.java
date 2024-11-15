package ru.yandex.practicum.avro.mapper.hub;

import org.mapstruct.Mapper;
import ru.yandex.practicum.avro.mapper.annotations.HubEventMapping;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.model.hub.DeviceAddedEvent;

@Mapper(componentModel = "spring", uses = DeviceAddedPayloadMapper.class)
public interface DeviceAddedEventMapper {
    @HubEventMapping
    HubEventAvro toAvro(DeviceAddedEvent event);
}
