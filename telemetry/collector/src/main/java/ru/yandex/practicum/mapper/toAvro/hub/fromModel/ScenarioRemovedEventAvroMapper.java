package ru.yandex.practicum.mapper.toAvro.hub.fromModel;

import org.mapstruct.Mapper;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.mapper.toAvro.hub.fromModel.annotations.HubEventMapping;
import ru.yandex.practicum.mapper.toAvro.hub.fromModel.payload.ScenarioRemovedPayloadAvroMapper;
import ru.yandex.practicum.model.hub.ScenarioRemovedEvent;

@Mapper(componentModel = "spring", uses = ScenarioRemovedPayloadAvroMapper.class)
public interface ScenarioRemovedEventAvroMapper {
    @HubEventMapping
    HubEventAvro toAvro(ScenarioRemovedEvent event);
}
