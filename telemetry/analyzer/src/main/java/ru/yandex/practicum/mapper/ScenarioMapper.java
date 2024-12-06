package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.grpc.telemetry.event.*;
import ru.yandex.practicum.model.entity.Scenario;

/**
 * Маппер для преобразования между Scenario и ScenarioAddedEventProto.
 */
@Mapper(componentModel = "spring", uses = {ScenarioConditionMapper.class, ScenarioActionMapper.class})
public interface ScenarioMapper {

    /**
     * Преобразует ScenarioAddedEventProto в Scenario.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "hubId", ignore = true)
    @Mapping(target = "conditions", source = "conditionsList")
    @Mapping(target = "actions", source = "actionsList")
    Scenario toEntity(ScenarioAddedEventProto proto);

    /**
     * Преобразует Scenario в ScenarioAddedEventProto.
     *
     * @param scenario объект Scenario.
     * @return объект ScenarioAddedEventProto.
     */
    default ScenarioAddedEventProto toProto(Scenario scenario) {
        return ScenarioAddedEventProto.newBuilder()
                .setName(scenario.getName()) // Название сценария.
                .addAllConditions(
                        scenario.getConditions().stream()
                                .map(condition -> ScenarioConditionProto.newBuilder()
                                        .setSensorId(condition.getSensor().getId())
                                        .setType(ConditionTypeProto.valueOf(condition.getCondition().getType().name()))
                                        .setOperation(ConditionOperationProto.valueOf(condition.getCondition().getOperation().name()))
                                        .setIntValue(condition.getCondition().getIntValue())
                                        .build()
                                ).toList()
                )
                .addAllActions(
                        scenario.getActions().stream()
                                .map(action -> DeviceActionProto.newBuilder()
                                        .setSensorId(action.getSensor().getId())
                                        .setType(ActionTypeProto.valueOf(action.getAction().getType().name()))
                                        .setValue(action.getAction().getValue())
                                        .build()
                                ).toList()
                )
                .build();
    }
}
