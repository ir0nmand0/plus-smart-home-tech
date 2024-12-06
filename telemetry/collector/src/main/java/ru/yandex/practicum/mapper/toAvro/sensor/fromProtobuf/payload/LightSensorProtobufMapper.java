package ru.yandex.practicum.mapper.toAvro.sensor.fromProtobuf.payload;

import org.mapstruct.Mapper;
import ru.yandex.practicum.grpc.telemetry.event.LightSensorProto;
import ru.yandex.practicum.kafka.telemetry.event.LightSensorAvro;

@Mapper(componentModel = "spring")
public interface LightSensorProtobufMapper {
    LightSensorAvro toAvro(LightSensorProto proto);
}
