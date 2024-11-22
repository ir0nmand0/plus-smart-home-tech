package ru.yandex.practicum.avro.mapper.hub;

import org.mapstruct.Mapper;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.model.hub.ScenarioCondition;

@Mapper(componentModel = "spring")
public interface ScenarioConditionMapper {
    ScenarioConditionAvro toAvro(ScenarioCondition condition);
}
