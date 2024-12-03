package ru.yandex.practicum.model.hub;

import lombok.Builder;

@Builder
public record DeviceAction(
        String sensorId,
        DeviceActionType type,
        Integer value
) {
}
