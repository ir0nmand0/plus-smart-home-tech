package ru.yandex.practicum.mapper.toAvro.hub.fromProtobuf.payload;

import org.mapstruct.Mapper;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;

@Mapper(componentModel = "spring")
public interface DeviceActionProtobufMapper {
    DeviceActionAvro toAvro(DeviceActionProto deviceAction);
}
