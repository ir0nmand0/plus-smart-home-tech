package ru.yandex.practicum.avro.mapper.sensor;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.model.sensor.MotionSensorEvent;

@Mapper(componentModel = "spring")
public interface MotionSensorPayloadMapper {
    @Named("mapToPayload")
    MotionSensorAvro toAvro(MotionSensorEvent event);
}
