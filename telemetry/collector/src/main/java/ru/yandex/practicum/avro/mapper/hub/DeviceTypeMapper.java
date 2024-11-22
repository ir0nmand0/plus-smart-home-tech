package ru.yandex.practicum.avro.mapper.hub;

import org.mapstruct.Mapper;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;
import ru.yandex.practicum.model.sensor.DeviceType;

@Mapper(componentModel = "spring")
public interface DeviceTypeMapper {
    DeviceTypeAvro toAvro(DeviceType deviceType);
}
