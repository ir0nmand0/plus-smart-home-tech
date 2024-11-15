package ru.yandex.practicum.avro.mapper.hub;

import org.mapstruct.Mapper;
import ru.yandex.practicum.avro.mapper.annotations.HubEventMapping;
import ru.yandex.practicum.kafka.telemetry.event.HubEventAvro;
import ru.yandex.practicum.model.hub.ScenarioAddedEvent;

/*
В MapStruct маппинг коллекций, таких как List, выполняется автоматически,
если элементы коллекции могут быть замаплены с помощью существующих методов.
*/
@Mapper(componentModel = "spring", uses = {ScenarioConditionMapper.class, DeviceActionMapper.class})
public interface ScenarioAddedEventMapper {
    @HubEventMapping
    HubEventAvro toAvro(ScenarioAddedEvent event);
}
