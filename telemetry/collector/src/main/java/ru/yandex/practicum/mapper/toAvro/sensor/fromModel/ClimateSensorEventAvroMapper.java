package ru.yandex.practicum.mapper.toAvro.sensor.fromModel;

import org.mapstruct.Mapper;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.mapper.toAvro.sensor.fromModel.annotations.SensorEventMapping;
import ru.yandex.practicum.mapper.toAvro.sensor.fromModel.payload.ClimateSensorPayloadAvroMapper;
import ru.yandex.practicum.model.sensor.ClimateSensorEvent;

@Mapper(componentModel = "spring", uses = ClimateSensorPayloadAvroMapper.class)
public interface ClimateSensorEventAvroMapper {
    @SensorEventMapping
    SensorEventAvro toAvro(ClimateSensorEvent event);
}
