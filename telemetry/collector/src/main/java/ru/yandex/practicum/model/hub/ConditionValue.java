package ru.yandex.practicum.model.hub;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

// Поле `oneof` для хранения значения условия
@JsonDeserialize(using = ConditionValueDeserializer.class)
public sealed interface ConditionValue permits BoolValue, IntValue {
}
