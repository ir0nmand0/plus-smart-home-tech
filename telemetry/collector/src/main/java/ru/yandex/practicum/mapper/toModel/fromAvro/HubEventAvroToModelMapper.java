package ru.yandex.practicum.mapper.toModel.fromAvro;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.mapper.toModel.factory.HubEventModelFactory;
import ru.yandex.practicum.model.hub.*;
import ru.yandex.practicum.model.sensor.DeviceType;

import java.util.Objects;

/**
 * Маппер для преобразования HubEventAvro в HubEvent.
 * <p>
 * Используются мапперы:
 * <ul>
 *     <li>Для `toModel` - {@link HubEventModelFactory}</li>
 * </ul>
 * Фабрика обязательна из-за невозможности напрямую создать наследников абстрактного класса.
 */
@Mapper(componentModel = "spring", uses = HubEventModelFactory.class)
public interface HubEventAvroToModelMapper {

    /**
     * Преобразует HubEventAvro в HubEvent.
     *
     * @param avro Avro-событие.
     * @return Модельное событие.
     */
    default HubEvent toModel(HubEventAvro avro) {
        return switch (avro.getPayload()) {
            case DeviceAddedEventAvro payload -> createDeviceAddedEvent(payload, avro);
            case DeviceRemovedEventAvro payload -> createDeviceRemovedEvent(payload, avro);
            case ScenarioAddedEventAvro payload -> createScenarioAddedEvent(payload, avro);
            case ScenarioRemovedEventAvro payload -> createScenarioRemovedEvent(payload, avro);
            case null, default ->
                    throw new IllegalArgumentException("Unsupported payload type: " + avro.getPayload().getClass());
        };
    }

    private DeviceAddedEvent createDeviceAddedEvent(DeviceAddedEventAvro payload, HubEventAvro avro) {
        return DeviceAddedEvent.builder()
                .hubId(avro.getHubId())
                .timestamp(avro.getTimestamp())
                .id(payload.getId())
                .type(DeviceType.valueOf(payload.getType().name()))
                .build();
    }

    private DeviceRemovedEvent createDeviceRemovedEvent(DeviceRemovedEventAvro payload, HubEventAvro avro) {
        return DeviceRemovedEvent.builder()
                .hubId(avro.getHubId())
                .timestamp(avro.getTimestamp())
                .id(payload.getId())
                .build();
    }

    private ScenarioAddedEvent createScenarioAddedEvent(ScenarioAddedEventAvro payload, HubEventAvro avro) {
        return ScenarioAddedEvent.builder()
                .hubId(avro.getHubId())
                .timestamp(avro.getTimestamp())
                .name(payload.getName())
                .conditions(payload.getConditions().stream()
                        .map(this::mapScenarioCondition)
                        .toList())
                .actions(payload.getActions().stream()
                        .map(this::mapDeviceAction)
                        .toList())
                .build();
    }

    private ScenarioRemovedEvent createScenarioRemovedEvent(ScenarioRemovedEventAvro payload, HubEventAvro avro) {
        return ScenarioRemovedEvent.builder()
                .hubId(avro.getHubId())
                .timestamp(avro.getTimestamp())
                .name(payload.getName())
                .build();
    }

    private ScenarioCondition mapScenarioCondition(ScenarioConditionAvro avro) {
        return ScenarioCondition.builder()
                .sensorId(avro.getSensorId())
                .type(ConditionType.valueOf(avro.getType().name()))
                .operation(ConditionOperation.valueOf(avro.getOperation().name()))
                .value(mapConditionValue(avro))
                .build();
    }

    @Named("mapConditionValue")
    default ConditionValue mapConditionValue(ScenarioConditionAvro avro) {
        if (Objects.isNull(avro.getValue())) {
            throw new IllegalArgumentException("Condition value is null");
        }

        return switch (avro.getValue()) {
            case Boolean boolValue -> new BoolValue(boolValue);
            case Integer intValue -> new IntValue(intValue);
            case null, default ->
                    throw new IllegalArgumentException("Unsupported condition value type: " + avro.getValue().getClass());
        };
    }

    private DeviceAction mapDeviceAction(DeviceActionAvro avro) {
        return DeviceAction.builder()
                .sensorId(avro.getSensorId())
                .type(DeviceActionType.valueOf(avro.getType().name()))
                .value(avro.getValue())
                .build();
    }
}
