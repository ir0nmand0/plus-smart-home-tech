package ru.yandex.practicum.model.hub;

public record ScenarioCondition(
        String sensorId,
        ConditionType type,
        ConditionOperation operation,
        int value
) {
}
