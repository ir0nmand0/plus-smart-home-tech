package ru.yandex.practicum.mapper.toAvro.hub.fromProtobuf.payload;

import org.mapstruct.Mapper;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioRemovedEventProto;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioRemovedEventAvro;

@Mapper(componentModel = "spring")
public interface ScenarioRemovedEventProtobufMapper {
    ScenarioRemovedEventAvro toAvro(ScenarioRemovedEventProto proto);
}
