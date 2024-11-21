package ru.yandex.practicum.avro.mapper.hub;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.model.hub.ScenarioRemovedEvent;

@Mapper(componentModel = "spring")
public interface ScenarioRemovedPayloadMapper {
    @Named("mapToPayload")
    ScenarioRemovedEventAvro toAvro(ScenarioRemovedEvent event);
}
