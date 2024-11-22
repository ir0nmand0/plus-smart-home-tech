package ru.yandex.practicum.avro.mapper.hub;

import org.mapstruct.Mapper;
import ru.yandex.practicum.avro.mapper.annotations.HubEventMapping;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.model.hub.DeviceRemovedEvent;

@Mapper(componentModel = "spring", uses = DeviceRemovedPayloadMapper.class)
public interface DeviceRemovedEventMapper {
    @HubEventMapping
    HubEventAvro toAvro(DeviceRemovedEvent event);
}
