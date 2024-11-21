package ru.yandex.practicum.avro.mapper.hub;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;
import ru.yandex.practicum.model.hub.DeviceRemovedEvent;

@Mapper(componentModel = "spring")
public interface DeviceRemovedPayloadMapper {
    @Named("mapToPayload")
    DeviceRemovedEventAvro toAvro(DeviceRemovedEvent event);
}
