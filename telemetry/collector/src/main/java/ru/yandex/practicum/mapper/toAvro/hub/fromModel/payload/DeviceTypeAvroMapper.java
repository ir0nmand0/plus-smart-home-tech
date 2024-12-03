package ru.yandex.practicum.mapper.toAvro.hub.fromModel.payload;

import org.mapstruct.Mapper;
import ru.yandex.practicum.kafka.telemetry.event.DeviceTypeAvro;
import ru.yandex.practicum.model.sensor.DeviceType;

@Mapper(componentModel = "spring")
public interface DeviceTypeAvroMapper {
    DeviceTypeAvro toAvro(DeviceType deviceType);
}
