package ru.yandex.practicum.mapper.toAvro.hub.fromModel;

import org.mapstruct.Mapper;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.mapper.toAvro.hub.fromModel.annotations.HubEventMapping;
import ru.yandex.practicum.mapper.toAvro.hub.fromModel.payload.DeviceRemovedPayloadAvroMapper;
import ru.yandex.practicum.model.hub.DeviceRemovedEvent;

@Mapper(componentModel = "spring", uses = DeviceRemovedPayloadAvroMapper.class)
public interface DeviceRemovedEventAvroMapper {
    @HubEventMapping
    HubEventAvro toAvro(DeviceRemovedEvent event);
}
