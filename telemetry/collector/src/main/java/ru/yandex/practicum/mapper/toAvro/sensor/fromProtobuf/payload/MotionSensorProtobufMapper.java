package ru.yandex.practicum.mapper.toAvro.sensor.fromProtobuf.payload;

import org.mapstruct.Mapper;
import ru.yandex.practicum.grpc.telemetry.event.MotionSensorProto;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;

@Mapper(componentModel = "spring")
public interface MotionSensorProtobufMapper {
    MotionSensorAvro toAvro(MotionSensorProto proto);
}
