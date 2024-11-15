package ru.yandex.practicum.avro.mapper.hub;

import org.mapstruct.Mapper;
import ru.yandex.practicum.avro.mapper.annotations.HubEventMapping;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.model.hub.ScenarioRemovedEvent;

@Mapper(componentModel = "spring", uses = ScenarioRemovedPayloadMapper.class)
public interface ScenarioRemovedEventMapper {
    @HubEventMapping
    HubEventAvro toAvro(ScenarioRemovedEvent event);
}
