package ru.yandex.practicum.mapper.toAvro.hub.fromModel;

import org.mapstruct.Mapper;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.mapper.toAvro.hub.fromModel.annotations.HubEventMapping;
import ru.yandex.practicum.mapper.toAvro.hub.fromModel.payload.DeviceAddedPayloadAvroMapper;
import ru.yandex.practicum.model.hub.DeviceAddedEvent;

@Mapper(componentModel = "spring", uses = DeviceAddedPayloadAvroMapper.class)
public interface DeviceAddedEventAvroMapper {
    @HubEventMapping
    HubEventAvro toAvro(DeviceAddedEvent event);
}
