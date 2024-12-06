package ru.yandex.practicum.mapper.toAvro.sensor.fromProtobuf.payload;

import org.mapstruct.Mapper;
import ru.yandex.practicum.grpc.telemetry.event.TemperatureSensorProto;
import ru.yandex.practicum.kafka.telemetry.event.TemperatureSensorAvro;

@Mapper(componentModel = "spring")
public interface TemperatureSensorProtobufMapper {
    TemperatureSensorAvro toAvro(TemperatureSensorProto proto);
}
