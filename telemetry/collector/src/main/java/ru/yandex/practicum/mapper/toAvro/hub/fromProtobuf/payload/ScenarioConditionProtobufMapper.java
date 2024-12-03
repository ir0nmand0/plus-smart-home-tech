package ru.yandex.practicum.mapper.toAvro.hub.fromProtobuf.payload;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
import ru.yandex.practicum.kafka.telemetry.event.ScenarioConditionAvro;

@Mapper(componentModel = "spring")
public interface ScenarioConditionProtobufMapper {
/*
    В сгенерированном классе Protobuf для ScenarioConditionProto поле value представлено как oneof,
    и для него создаётся метод getValueCase()
    В @Mapping(target = "value", source = ".") используется .
    для передачи всего объекта ScenarioConditionProto в метод mapValueToAvro или mapValueToModel
*/

    @Mapping(target = "value", source = ".", qualifiedByName = "mapValueToAvro")
    ScenarioConditionAvro toAvro(ScenarioConditionProto condition);

    @Named("mapValueToAvro")
    default Object mapValueToAvro(ScenarioConditionProto proto) {
        return switch (proto.getValueCase()) {
            case BOOL_VALUE -> proto.getBoolValue();
            case INT_VALUE -> proto.getIntValue();
            default -> null;
        };
    }
}
