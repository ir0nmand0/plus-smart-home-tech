package ru.yandex.practicum.avro.mapper.hub;

import org.mapstruct.Mapper;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.model.hub.ScenarioRemovedEvent;

@Mapper(componentModel = "spring")
public interface ScenarioRemovedPayloadMapper {
    ScenarioRemovedEventAvro toAvro(ScenarioRemovedEvent event);
}
