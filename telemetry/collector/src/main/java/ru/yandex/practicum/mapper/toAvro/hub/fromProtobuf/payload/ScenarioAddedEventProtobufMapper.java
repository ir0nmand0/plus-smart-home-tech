package ru.yandex.practicum.mapper.toAvro.hub.fromProtobuf.payload;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioAddedEventProto;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioAddedEventAvro;

@Mapper(componentModel = "spring", uses = {ScenarioConditionProtobufMapper.class, DeviceActionProtobufMapper.class})
public interface ScenarioAddedEventProtobufMapper {

    /**
     * Маппинг события добавления сценария из Protobuf в Avro.
     */
    @Mapping(source = "conditionsList", target = "conditions")
    @Mapping(source = "actionsList", target = "actions")
    ScenarioAddedEventAvro toAvro(ScenarioAddedEventProto proto);
}

