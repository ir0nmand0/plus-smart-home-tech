package ru.yandex.practicum.mapper.toAvro.sensor.fromModel.payload;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import ru.yandex.practicum.kafka.telemetry.event.MotionSensorAvro;
import ru.yandex.practicum.model.sensor.MotionSensorEvent;

@Mapper(componentModel = "spring")
public interface MotionSensorPayloadAvroMapper {
    @Named("mapToPayload")
    MotionSensorAvro toAvro(MotionSensorEvent event);
}
