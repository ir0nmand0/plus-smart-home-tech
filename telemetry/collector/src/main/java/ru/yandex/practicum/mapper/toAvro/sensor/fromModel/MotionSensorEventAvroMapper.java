package ru.yandex.practicum.mapper.toAvro.sensor.fromModel;

import org.mapstruct.Mapper;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.mapper.toAvro.sensor.fromModel.annotations.SensorEventMapping;
import ru.yandex.practicum.mapper.toAvro.sensor.fromModel.payload.MotionSensorPayloadAvroMapper;
import ru.yandex.practicum.model.sensor.MotionSensorEvent;

@Mapper(componentModel = "spring", uses = MotionSensorPayloadAvroMapper.class)
public interface MotionSensorEventAvroMapper {
    @SensorEventMapping
    SensorEventAvro toAvro(MotionSensorEvent event);
}
