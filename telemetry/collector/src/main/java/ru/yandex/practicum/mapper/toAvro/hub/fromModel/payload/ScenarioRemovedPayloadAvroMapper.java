package ru.yandex.practicum.mapper.toAvro.hub.fromModel.payload;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;
import ru.yandex.practicum.model.hub.ScenarioRemovedEvent;

@Mapper(componentModel = "spring")
public interface ScenarioRemovedPayloadAvroMapper {
    @Named("mapToPayload")
    ScenarioRemovedEventAvro toAvro(ScenarioRemovedEvent event);
}
