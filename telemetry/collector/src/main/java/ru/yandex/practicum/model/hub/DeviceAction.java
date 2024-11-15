package ru.yandex.practicum.model.hub;

public record DeviceAction(
        String sensorId,
        DeviceActionType type,
        int value
) {
}
