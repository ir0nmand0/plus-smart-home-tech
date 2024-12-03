package ru.yandex.practicum.mapper.toAvro.sensor.fromModel;

import org.mapstruct.Mapper;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.mapper.toAvro.sensor.fromModel.annotations.SensorEventMapping;
import ru.yandex.practicum.mapper.toAvro.sensor.fromModel.payload.TemperatureSensorPayloadAvroMapper;
import ru.yandex.practicum.model.sensor.TemperatureSensorEvent;

@Mapper(componentModel = "spring", uses = TemperatureSensorPayloadAvroMapper.class)
public interface TemperatureSensorEventAvroMapper {
    @SensorEventMapping
    SensorEventAvro toAvro(TemperatureSensorEvent event);
}