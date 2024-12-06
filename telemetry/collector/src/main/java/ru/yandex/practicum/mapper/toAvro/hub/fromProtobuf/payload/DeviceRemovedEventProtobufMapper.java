package ru.yandex.practicum.mapper.toAvro.hub.fromProtobuf.payload;

import org.mapstruct.Mapper;
import ru.yandex.practicum.grpc.telemetry.event.DeviceAddedEventProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceRemovedEventAvro;

@Mapper(componentModel = "spring")
public interface DeviceRemovedEventProtobufMapper {
    DeviceRemovedEventAvro toAvro(DeviceAddedEventProto proto);
}
