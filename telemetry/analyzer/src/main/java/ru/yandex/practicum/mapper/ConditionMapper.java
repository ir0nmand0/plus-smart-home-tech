package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.grpc.telemetry.event.ConditionOperationProto;
import ru.yandex.practicum.grpc.telemetry.event.ConditionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
import ru.yandex.practicum.model.entity.Condition;
import ru.yandex.practicum.model.entity.ValueType;

@Mapper(componentModel = "spring")
public interface ConditionMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "valueType", expression = "java(determineValueType(proto))")
    @Mapping(target = "boolValue", expression = "java(proto.hasBoolValue() ? proto.getBoolValue() : null)")
    @Mapping(target = "intValue", expression = "java(proto.hasIntValue() ? proto.getIntValue() : null)")
    Condition toEntity(ScenarioConditionProto proto);

    default ScenarioConditionProto toProto(Condition condition) {
        ScenarioConditionProto.Builder builder = ScenarioConditionProto.newBuilder()
                .setType(ConditionTypeProto.valueOf(condition.getType().name()))
                .setOperation(ConditionOperationProto.valueOf(condition.getOperation().name()));

        // Маппинг значения на основе его типа
        if (condition.getValueType() == ValueType.BOOL && condition.getBoolValue() != null) {
            builder.setBoolValue(condition.getBoolValue());
        } else if (condition.getValueType() == ValueType.INT && condition.getIntValue() != null) {
            builder.setIntValue(condition.getIntValue());
        }

        return builder.build();
    }

    default ValueType determineValueType(ScenarioConditionProto proto) {
        if (proto.hasBoolValue()) {
            return ValueType.BOOL;
        } else if (proto.hasIntValue()) {
            return ValueType.INT;
        }
        return null; // Если значение не задано
    }
}
