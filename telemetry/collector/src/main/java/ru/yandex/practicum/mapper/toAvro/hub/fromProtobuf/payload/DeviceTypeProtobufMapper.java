package ru.yandex.practicum.mapper.toAvro.hub.fromProtobuf.payload;

import org.mapstruct.Mapper;
import ru.yandex.practicum.grpc.telemetry.event.DeviceTypeProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;

@Mapper(componentModel = "spring")
public interface DeviceTypeProtobufMapper {
    DeviceTypeAvro toAvro(DeviceTypeProto proto);
}
