package ru.yandex.practicum.mapper.toAvro.hub.fromModel.payload;

import org.mapstruct.Mapper;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;
import ru.yandex.practicum.model.hub.ScenarioCondition;

@Mapper(componentModel = "spring")
public interface ScenarioConditionAvroMapper {
    ScenarioConditionAvro toAvro(ScenarioCondition condition);
}
