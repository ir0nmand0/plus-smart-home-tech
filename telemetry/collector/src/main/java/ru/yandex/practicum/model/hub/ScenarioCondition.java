package ru.yandex.practicum.model.hub;

import lombok.Builder;

@Builder
public record ScenarioCondition(
        String sensorId,
        ConditionType type,
        ConditionOperation operation,
        ConditionValue value
) {
}
