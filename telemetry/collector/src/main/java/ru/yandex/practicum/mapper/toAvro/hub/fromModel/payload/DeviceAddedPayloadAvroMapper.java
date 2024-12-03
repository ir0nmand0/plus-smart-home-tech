package ru.yandex.practicum.mapper.toAvro.hub.fromModel.payload;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.model.hub.DeviceAddedEvent;

@Mapper(componentModel = "spring", uses = DeviceTypeAvroMapper.class)
public interface DeviceAddedPayloadAvroMapper {
    @Named("mapToPayload")
    DeviceAddedEventAvro toAvro(DeviceAddedEvent event);
}
