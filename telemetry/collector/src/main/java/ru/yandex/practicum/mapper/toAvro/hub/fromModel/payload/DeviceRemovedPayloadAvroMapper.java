package ru.yandex.practicum.mapper.toAvro.hub.fromModel.payload;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.model.hub.DeviceRemovedEvent;

@Mapper(componentModel = "spring")
public interface DeviceRemovedPayloadAvroMapper {
    @Named("mapToPayload")
    DeviceRemovedEventAvro toAvro(DeviceRemovedEvent event);
}
