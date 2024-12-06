package ru.yandex.practicum.mapper.toAvro.hub.fromModel;

import org.mapstruct.Mapper;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.mapper.toAvro.hub.fromModel.annotations.HubEventMapping;
import ru.yandex.practicum.mapper.toAvro.hub.fromModel.payload.ScenarioAddedPayloadAvroMapper;
import ru.yandex.practicum.model.hub.ScenarioAddedEvent;

/*
В MapStruct маппинг коллекций, таких как List, выполняется автоматически,
если элементы коллекции могут быть замаплены с помощью существующих методов.
*/
@Mapper(componentModel = "spring", uses = ScenarioAddedPayloadAvroMapper.class)
public interface ScenarioAddedEventAvroMapper {
    @HubEventMapping
    HubEventAvro toAvro(ScenarioAddedEvent event);
}
