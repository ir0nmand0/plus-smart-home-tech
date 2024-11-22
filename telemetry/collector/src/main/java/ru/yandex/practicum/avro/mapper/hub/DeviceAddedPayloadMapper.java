package ru.yandex.practicum.avro.mapper.hub;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.model.hub.DeviceAddedEvent;

@Mapper(componentModel = "spring", uses = DeviceTypeMapper.class)
public interface DeviceAddedPayloadMapper {
    @Named("mapToPayload")
    DeviceAddedEventAvro toAvro(DeviceAddedEvent event);
}
