package ru.yandex.practicum.avro.mapper.sensor;

import org.mapstruct.Mapper;
import ru.yandex.practicum.avro.mapper.annotations.SensorEventMapping;
import ru.yandex.practicum.kafka.telemetry.event.SensorEventAvro;
import ru.yandex.practicum.model.sensor.SwitchSensorEvent;

@Mapper(componentModel = "spring", uses = SwitchSensorPayloadMapper.class)
public interface SwitchSensorEventMapper {
    @SensorEventMapping
    SensorEventAvro toAvro(SwitchSensorEvent event);
}