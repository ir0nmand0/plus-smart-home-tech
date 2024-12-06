package ru.yandex.practicum.mapper.toProtobuf.hub.fromModel;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.grpc.telemetry.event.*;
import ru.yandex.practicum.mapper.helper.TimestampProtobufMapper;
import ru.yandex.practicum.model.hub.HubEvent;
import ru.yandex.practicum.model.hub.*;

import java.util.List;

/**
 * Маппер для преобразования между HubEventProto и HubEvent (модель).
 */
@Mapper(componentModel = "spring", uses = TimestampProtobufMapper.class)
public interface HubEventProtobufMapper {
    /**
     * Преобразует модельное событие HubEvent обратно в HubEventProto.
     * <p>
     * Использует фабрику для создания соответствующих объектов Protobuf.
     *
     * @param event Модельное событие.
     * @return Protobuf-событие.
     */
    @Mapping(source = "timestamp", target = "timestamp", qualifiedByName = "instantToTimestamp")
    default HubEventProto toProtobuf(HubEvent event) {
        HubEventProto.Builder builder = HubEventProto.newBuilder()
                .setHubId(event.getHubId());

        switch (event) {
            case DeviceAddedEvent deviceAddedEvent -> {
                builder.setDeviceAdded(toDeviceAddedEventProto(deviceAddedEvent));
            }
            case DeviceRemovedEvent deviceRemovedEvent -> {
                builder.setDeviceRemoved(toDeviceRemovedEventProto(deviceRemovedEvent));
            }
            case ScenarioAddedEvent scenarioAddedEvent -> {
                builder.setScenarioAdded(toScenarioAddedEventProto(scenarioAddedEvent));
            }
            case ScenarioRemovedEvent scenarioRemovedEvent -> {
                builder.setScenarioRemoved(toScenarioRemovedEventProto(scenarioRemovedEvent));
            }
            default -> {
                throw new IllegalArgumentException("Unsupported HubEvent type: " + event.getClass().getSimpleName());
            }
        }

        return builder.build();
    }

    // Методы для маппинга модели -> Protobuf

    private DeviceAddedEventProto toDeviceAddedEventProto(DeviceAddedEvent event) {
        return DeviceAddedEventProto.newBuilder()
                .setId(event.getId())
                .setType(DeviceTypeProto.valueOf(event.getType().name()))
                .build();
    }

    private DeviceRemovedEventProto toDeviceRemovedEventProto(DeviceRemovedEvent event) {
        return DeviceRemovedEventProto.newBuilder()
                .setId(event.getId())
                .build();
    }

    private ScenarioAddedEventProto toScenarioAddedEventProto(ScenarioAddedEvent event) {
        return ScenarioAddedEventProto.newBuilder()
                .setName(event.getName())
                .addAllConditions(toScenarioConditionProtos(event.getConditions()))
                .addAllActions(toDeviceActionProtos(event.getActions()))
                .build();
    }

    private ScenarioRemovedEventProto toScenarioRemovedEventProto(ScenarioRemovedEvent event) {
        return ScenarioRemovedEventProto.newBuilder()
                .setName(event.getName())
                .build();
    }

    private List<ScenarioConditionProto> toScenarioConditionProtos(List<ScenarioCondition> conditions) {
        return conditions.stream()
                .map(this::toScenarioConditionProto)
                .toList();
    }

    private ScenarioConditionProto toScenarioConditionProto(ScenarioCondition condition) {
        ScenarioConditionProto.Builder builder = ScenarioConditionProto.newBuilder()
                .setSensorId(condition.sensorId())
                .setType(ConditionTypeProto.valueOf(condition.type().name()))
                .setOperation(ConditionOperationProto.valueOf(condition.operation().name()));

        switch (condition.value()) {
            case BoolValue boolValue -> builder.setBoolValue(boolValue.value());
            case IntValue intValue -> builder.setIntValue(intValue.value());
            case null, default -> {
            }
        }

        return builder.build();
    }

    private List<DeviceActionProto> toDeviceActionProtos(List<DeviceAction> actions) {
        return actions.stream()
                .map(this::toDeviceActionProto)
                .toList();
    }

    private DeviceActionProto toDeviceActionProto(DeviceAction action) {
        DeviceActionProto.Builder builder = DeviceActionProto.newBuilder()
                .setSensorId(action.sensorId())
                .setType(ActionTypeProto.valueOf(action.type().name()));

        if (action.value() != null) {
            builder.setValue(action.value());
        }

        return builder.build();
    }
}
