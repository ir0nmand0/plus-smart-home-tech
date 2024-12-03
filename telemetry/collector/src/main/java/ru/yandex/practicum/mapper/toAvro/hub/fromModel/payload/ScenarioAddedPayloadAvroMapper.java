package ru.yandex.practicum.mapper.toAvro.hub.fromModel.payload;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.model.hub.ScenarioAddedEvent;

@Mapper(componentModel = "spring", uses = {ScenarioConditionAvroMapper.class, DeviceActionAvroMapper.class})
public interface ScenarioAddedPayloadAvroMapper {
    @Named("mapToPayload")
    ScenarioAddedEventAvro toAvro(ScenarioAddedEvent scenarioAddedEvent);
}
