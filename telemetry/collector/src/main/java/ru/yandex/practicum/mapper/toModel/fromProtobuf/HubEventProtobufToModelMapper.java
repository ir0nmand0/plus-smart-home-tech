package ru.yandex.practicum.mapper.toModel.fromProtobuf;

import org.mapstruct.Mapper;
import ru.yandex.practicum.grpc.telemetry.event.*;
import ru.yandex.practicum.mapper.toModel.factory.HubEventModelFactory;
import ru.yandex.practicum.model.hub.HubEvent;
import ru.yandex.practicum.model.hub.*;
import ru.yandex.practicum.model.sensor.DeviceType;

import java.time.Instant;
import java.util.List;

@Mapper(componentModel = "spring", uses = HubEventModelFactory.class)
public interface HubEventProtobufToModelMapper {
    /**
     * Преобразует HubEventProto в модельное событие HubEvent.
     * <p>
     * Использует фабрику {@code HubEventFactory}, так как классы-наследники абстрактного класса {@code HubEvent}
     * не могут быть созданы MapStruct автоматически.
     *
     * @param proto Protobuf-событие.
     * @return Модельное событие.
     */
    default HubEvent toModel(HubEventProto proto) {
        Instant timestamp = Instant.ofEpochSecond(proto.getTimestamp().getSeconds(), proto.getTimestamp().getNanos());
        return switch (proto.getPayloadCase()) {
            case DEVICE_ADDED -> createDeviceAddedEvent(proto, timestamp);
            case DEVICE_REMOVED -> createDeviceRemovedEvent(proto, timestamp);
            case SCENARIO_ADDED -> createScenarioAddedEvent(proto, timestamp);
            case SCENARIO_REMOVED -> createScenarioRemovedEvent(proto, timestamp);
            default -> throw new IllegalArgumentException("Unsupported payload type: " + proto.getPayloadCase());
        };
    }

    // Методы для маппинга Protobuf -> Модель

    /**
     * Создаёт объект DeviceAddedEvent из Protobuf.
     */
    private DeviceAddedEvent createDeviceAddedEvent(HubEventProto proto, Instant timestamp) {
        return DeviceAddedEvent.builder()
                .hubId(proto.getHubId())
                .timestamp(timestamp)
                .id(proto.getDeviceAdded().getId())
                .type(mapDeviceType(proto.getDeviceAdded().getType()))
                .build();
    }

    /**
     * Создаёт объект DeviceRemovedEvent из Protobuf.
     */
    private DeviceRemovedEvent createDeviceRemovedEvent(HubEventProto proto, Instant timestamp) {
        return DeviceRemovedEvent.builder()
                .hubId(proto.getHubId())
                .timestamp(timestamp)
                .id(proto.getDeviceRemoved().getId())
                .build();
    }

    /**
     * Создаёт объект ScenarioAddedEvent из Protobuf.
     */
    private ScenarioAddedEvent createScenarioAddedEvent(HubEventProto proto, Instant timestamp) {
        return ScenarioAddedEvent.builder()
                .hubId(proto.getHubId())
                .timestamp(timestamp)
                .name(proto.getScenarioAdded().getName())
                .conditions(mapConditions(proto.getScenarioAdded().getConditionsList()))
                .actions(mapActions(proto.getScenarioAdded().getActionsList()))
                .build();
    }

    /**
     * Создаёт объект ScenarioRemovedEvent из Protobuf.
     */
    private ScenarioRemovedEvent createScenarioRemovedEvent(HubEventProto proto, Instant timestamp) {
        return ScenarioRemovedEvent.builder()
                .hubId(proto.getHubId())
                .timestamp(timestamp)
                .name(proto.getScenarioRemoved().getName())
                .build();
    }

    // Методы для маппинга списков и значений

    /**
     * Маппинг списка условий из Protobuf в модель.
     */
    private List<ScenarioCondition> mapConditions(List<ScenarioConditionProto> protoConditions) {
        return protoConditions.stream()
                .map(this::mapCondition)
                .toList();
    }

    /**
     * Маппинг одного условия из Protobuf в модель.
     */
    private ScenarioCondition mapCondition(ScenarioConditionProto condition) {
        return ScenarioCondition.builder()
                .sensorId(condition.getSensorId())
                .type(mapConditionType(condition.getType()))
                .operation(mapConditionOperation(condition.getOperation()))
                .value(mapConditionValue(condition))
                .build();
    }

    /**
     * Преобразует список действий из Protobuf в модель.
     */
    private List<DeviceAction> mapActions(List<DeviceActionProto> protoActions) {
        return protoActions.stream()
                .map(this::mapAction)
                .toList();
    }

    /**
     * Преобразует одно действие из Protobuf в модель.
     */
    private DeviceAction mapAction(DeviceActionProto action) {
        return DeviceAction.builder()
                .sensorId(action.getSensorId())
                .type(mapActionType(action.getType()))
                .value(action.hasValue() ? action.getValue() : null)
                .build();
    }

    // Методы для преобразования типов

    /**
     * Преобразует ConditionType из Protobuf в модель.
     */
    private ConditionType mapConditionType(ConditionTypeProto typeProto) {
        return ConditionType.valueOf(typeProto.name());
    }

    /**
     * Преобразует ConditionOperation из Protobuf в модель.
     */
    private ConditionOperation mapConditionOperation(ConditionOperationProto operationProto) {
        return ConditionOperation.valueOf(operationProto.name());
    }

    /**
     * Преобразует ActionType из Protobuf в модель.
     */
    private DeviceActionType mapActionType(ActionTypeProto typeProto) {
        return DeviceActionType.valueOf(typeProto.name());
    }

    /**
     * Преобразует DeviceType из Protobuf в модель.
     */
    private DeviceType mapDeviceType(DeviceTypeProto typeProto) {
        return DeviceType.valueOf(typeProto.name());
    }

    /**
     * Преобразует значение условия из Protobuf в модель.
     * <p>
     * Используется для определения типа значения условия (например, {@code BOOL_VALUE} или {@code INT_VALUE})
     * и создания соответствующего объекта модели.
     *
     * @param condition Protobuf-условие.
     * @return Объект значения условия модели.
     */
    private ConditionValue mapConditionValue(ScenarioConditionProto condition) {
        return switch (condition.getValueCase()) {
            case BOOL_VALUE -> new BoolValue(condition.getBoolValue());
            case INT_VALUE -> new IntValue(condition.getIntValue());
            default ->
                    throw new IllegalArgumentException("Unsupported condition value type: " + condition.getValueCase());
        };
    }
}
