package ru.yandex.practicum.mapper.toAvro.sensor.fromModel;

import org.mapstruct.Mapper;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.mapper.toAvro.sensor.fromModel.annotations.SensorEventMapping;
import ru.yandex.practicum.mapper.toAvro.sensor.fromModel.payload.SwitchSensorPayloadAvroMapper;
import ru.yandex.practicum.model.sensor.SwitchSensorEvent;

@Mapper(componentModel = "spring", uses = SwitchSensorPayloadAvroMapper.class)
public interface SwitchSensorEventAvroMapper {
    @SensorEventMapping
    SensorEventAvro toAvro(SwitchSensorEvent event);
}