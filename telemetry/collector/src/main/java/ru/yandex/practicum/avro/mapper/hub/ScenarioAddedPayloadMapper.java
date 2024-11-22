package ru.yandex.practicum.avro.mapper.hub;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;
import ru.yandex.practicum.model.hub.ScenarioAddedEvent;

@Mapper(componentModel = "spring", uses = {ScenarioConditionMapper.class, DeviceActionMapper.class})
public interface ScenarioAddedPayloadMapper {
    @Named("mapToPayload")
    ScenarioAddedEventAvro toAvro(ScenarioAddedEvent scenarioAddedEvent);
}
