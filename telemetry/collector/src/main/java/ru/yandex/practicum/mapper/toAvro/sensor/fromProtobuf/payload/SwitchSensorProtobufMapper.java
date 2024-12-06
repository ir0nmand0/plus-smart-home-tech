package ru.yandex.practicum.mapper.toAvro.sensor.fromProtobuf.payload;

import org.mapstruct.Mapper;
import ru.yandex.practicum.grpc.telemetry.event.SwitchSensorProto;
import ru.yandex.practicum.kafka.telemetry.event.SwitchSensorAvro;

@Mapper(componentModel = "spring")
public interface SwitchSensorProtobufMapper {
    SwitchSensorAvro toAvro(SwitchSensorProto proto);
}
