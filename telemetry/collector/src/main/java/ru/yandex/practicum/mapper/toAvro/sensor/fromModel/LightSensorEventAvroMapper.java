package ru.yandex.practicum.mapper.toAvro.sensor.fromModel;

import org.mapstruct.Mapper;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.mapper.toAvro.sensor.fromModel.annotations.SensorEventMapping;
import ru.yandex.practicum.mapper.toAvro.sensor.fromModel.payload.LightSensorPayloadAvroMapper;
import ru.yandex.practicum.model.sensor.LightSensorEvent;

@Mapper(componentModel = "spring", uses = LightSensorPayloadAvroMapper.class)
public interface LightSensorEventAvroMapper {
    @SensorEventMapping
    SensorEventAvro toAvro(LightSensorEvent event);
}