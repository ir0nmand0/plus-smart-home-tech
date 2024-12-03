package ru.yandex.practicum.mapper.toAvro.hub.fromProtobuf.payload;

import org.mapstruct.Mapper;
import ru.yandex.practicum.grpc.telemetry.event.DeviceAddedEventProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceAddedEventAvro;
import ru.yandex.practicum.mapper.helper.TimestampProtobufMapper;

@Mapper(componentModel = "spring", uses = {DeviceTypeProtobufMapper.class, TimestampProtobufMapper.class})
public interface DeviceAddedEventProtobufMapper {
    DeviceAddedEventAvro toAvro(DeviceAddedEventProto proto);
}
