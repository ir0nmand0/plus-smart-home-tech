package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.grpc.telemetry.event.DeviceAddedEventProto;
import ru.yandex.practicum.grpc.telemetry.event.HubEventProto;
import ru.yandex.practicum.model.entity.Sensor;

/**
 * Маппер для преобразования между Sensor и HubEventProto.
 */
@Mapper(componentModel = "spring")
public interface SensorMapper {

    /**
     * Преобразует HubEventProto в Sensor.
     *
     * @param proto объект HubEventProto.
     * @return объект Sensor или null, если данные некорректны.
     */
    @Mapping(target = "id", ignore = true)
    // Игнорируем поле id
    Sensor toEntity(HubEventProto proto);

    /**
     * Преобразует Sensor в HubEventProto.
     *
     * @param sensor объект Sensor.
     * @return объект HubEventProto или null, если данные некорректны.
     */
    default HubEventProto toProto(Sensor sensor) {
        if (sensor == null || sensor.getId() == null || sensor.getHubId() == null) {
            return null;
        }

        return HubEventProto.newBuilder()
                .setHubId(sensor.getHubId())
                .setDeviceAdded(DeviceAddedEventProto.newBuilder()
                        .setId(sensor.getId())
                        .build())
                .build();
    }
}
