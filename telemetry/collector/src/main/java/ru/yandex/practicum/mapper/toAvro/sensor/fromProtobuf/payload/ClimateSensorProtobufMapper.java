package ru.yandex.practicum.mapper.toAvro.sensor.fromProtobuf.payload;

import org.mapstruct.Mapper;
import ru.yandex.practicum.grpc.telemetry.event.ClimateSensorProto;
import ru.yandex.practicum.kafka.telemetry.event.ClimateSensorAvro;

@Mapper(componentModel = "spring")
public interface ClimateSensorProtobufMapper {
    ClimateSensorAvro toAvro(ClimateSensorProto proto);
}
