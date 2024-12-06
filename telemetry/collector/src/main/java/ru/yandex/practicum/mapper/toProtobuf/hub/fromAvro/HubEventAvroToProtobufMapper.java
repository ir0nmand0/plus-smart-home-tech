package ru.yandex.practicum.mapper.toProtobuf.hub.fromAvro;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.grpc.telemetry.event.*;
import ru.yandex.practicum.kafka.telemetry.event.*;
import ru.yandex.practicum.mapper.helper.TimestampProtobufMapper;

import java.util.ArrayList;
import java.util.List;

/**
 * Маппер для преобразования объектов Avro HubEventAvro в Protobuf HubEventProto.
 * <p>
 * Использует маппер для временных меток {@link TimestampProtobufMapper}.
 */
@Mapper(componentModel = "spring", uses = TimestampProtobufMapper.class)
public interface HubEventAvroToProtobufMapper {

    /**
     * Преобразует объект Avro HubEventAvro в Protobuf HubEventProto.
     *
     * @param avro Avro-событие HubEventAvro.
     * @return Protobuf-событие HubEventProto.
     */
    @Mapping(source = "timestamp", target = "timestamp", qualifiedByName = "instantToTimestamp")
    default HubEventProto toProtobuf(HubEventAvro avro) {
        HubEventProto.Builder builder = HubEventProto.newBuilder()
                .setHubId(avro.getHubId());

        switch (avro.getPayload()) {
            case DeviceAddedEventAvro deviceAddedPayload ->
                    builder.setDeviceAdded(toDeviceAddedEventProto(deviceAddedPayload));
            case DeviceRemovedEventAvro deviceRemovedPayload ->
                    builder.setDeviceRemoved(toDeviceRemovedEventProto(deviceRemovedPayload));
            case ScenarioAddedEventAvro scenarioAddedPayload ->
                    builder.setScenarioAdded(toScenarioAddedEventProto(scenarioAddedPayload));
            case ScenarioRemovedEventAvro scenarioRemovedPayload ->
                    builder.setScenarioRemoved(toScenarioRemovedEventProto(scenarioRemovedPayload));
            case null, default -> throw new IllegalArgumentException("Unsupported HubEventAvro payload type: "
                    + avro.getPayload().getClass());
        }

        return builder.build();
    }

    /**
     * Преобразует DeviceAddedEventAvro в DeviceAddedEventProto.
     */
    default DeviceAddedEventProto toDeviceAddedEventProto(DeviceAddedEventAvro avro) {
        return DeviceAddedEventProto.newBuilder()
                .setId(avro.getId())
                .setType(DeviceTypeProto.valueOf(avro.getType().name()))
                .build();
    }

    /**
     * Преобразует DeviceRemovedEventAvro в DeviceRemovedEventProto.
     */
    default DeviceRemovedEventProto toDeviceRemovedEventProto(DeviceRemovedEventAvro avro) {
        return DeviceRemovedEventProto.newBuilder()
                .setId(avro.getId())
                .build();
    }

    /**
     * Преобразует ScenarioAddedEventAvro в ScenarioAddedEventProto.
     */
    default ScenarioAddedEventProto toScenarioAddedEventProto(ScenarioAddedEventAvro avro) {
        return ScenarioAddedEventProto.newBuilder()
                .setName(avro.getName())
                .addAllConditions(toScenarioConditionProtos(avro.getConditions()))
                .addAllActions(toDeviceActionProtos(avro.getActions()))
                .build();
    }

    /**
     * Преобразует ScenarioRemovedEventAvro в ScenarioRemovedEventProto.
     */
    default ScenarioRemovedEventProto toScenarioRemovedEventProto(ScenarioRemovedEventAvro avro) {
        return ScenarioRemovedEventProto.newBuilder()
                .setName(avro.getName())
                .build();
    }

    /**
     * Преобразует список условий из Avro в Protobuf.
     */
    private Iterable<ScenarioConditionProto> toScenarioConditionProtos(Iterable<ScenarioConditionAvro> conditions) {
        List<ScenarioConditionProto> result = new ArrayList<>();
        conditions.forEach(condition -> result.add(toScenarioConditionProto(condition)));
        return result;
    }

    /**
     * Преобразует одно условие из Avro в Protobuf.
     */
    private ScenarioConditionProto toScenarioConditionProto(ScenarioConditionAvro avro) {
        ScenarioConditionProto.Builder builder = ScenarioConditionProto.newBuilder()
                .setSensorId(avro.getSensorId())
                .setType(ConditionTypeProto.valueOf(avro.getType().name()))
                .setOperation(ConditionOperationProto.valueOf(avro.getOperation().name()));

        switch (avro.getValue()) {
            case Boolean boolValue -> builder.setBoolValue(boolValue);
            case Integer intValue -> builder.setIntValue(intValue);
            case null, default -> {
            }
        }

        return builder.build();
    }

    /**
     * Преобразует список действий из Avro в Protobuf.
     */
    private Iterable<DeviceActionProto> toDeviceActionProtos(Iterable<DeviceActionAvro> actions) {
        List<DeviceActionProto> result = new ArrayList<>();
        actions.forEach(action -> result.add(toDeviceActionProto(action)));
        return result;
    }

    /**
     * Преобразует одно действие из Avro в Protobuf.
     */
    private DeviceActionProto toDeviceActionProto(DeviceActionAvro avro) {
        DeviceActionProto.Builder builder = DeviceActionProto.newBuilder()
                .setSensorId(avro.getSensorId())
                .setType(ActionTypeProto.valueOf(avro.getType().name()));

        if (avro.getValue() != null) {
            builder.setValue(avro.getValue());
        }

        return builder.build();
    }
}

