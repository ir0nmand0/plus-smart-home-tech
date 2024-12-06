package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.grpc.telemetry.event.ConditionOperationProto;
import ru.yandex.practicum.grpc.telemetry.event.ConditionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.ScenarioConditionProto;
import ru.yandex.practicum.model.entity.ScenarioCondition;

@Mapper(componentModel = "spring", uses = {EnumMapper.class})
public interface ScenarioConditionMapper {

    /**
     * Преобразует ScenarioConditionProto в ScenarioCondition.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "scenario", ignore = true)
    @Mapping(target = "sensor.id", source = "sensorId")
    @Mapping(target = "condition", ignore = true)
    // Игнорируем Condition, так как его нет в Protobuf.
    ScenarioCondition toEntity(ScenarioConditionProto proto);

    /**
     * Преобразует ScenarioCondition в ScenarioConditionProto.
     */
    default ScenarioConditionProto toProto(ScenarioCondition entity) {
        ScenarioConditionProto.Builder builder = ScenarioConditionProto.newBuilder()
                .setSensorId(entity.getSensor().getId())
                .setType(ConditionTypeProto.valueOf(entity.getCondition().getType().name()))
                .setOperation(ConditionOperationProto.valueOf(entity.getCondition().getOperation().name()));

        if (entity.getCondition().getIntValue() != null) {
            builder.setIntValue(entity.getCondition().getIntValue());
        }

        if (entity.getCondition().getBoolValue() != null) {
            builder.setBoolValue(entity.getCondition().getBoolValue());
        }

        return builder.build();
    }
}
