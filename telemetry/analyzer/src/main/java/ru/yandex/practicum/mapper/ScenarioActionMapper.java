package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.kafka.telemetry.event.DeviceActionAvro;
import ru.yandex.practicum.model.entity.ScenarioAction;

@Mapper(componentModel = "spring", uses = EnumMapper.class)
public interface ScenarioActionMapper {

    /**
     * Преобразует DeviceActionProto в ScenarioAction.
     */
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "scenario", ignore = true)
    @Mapping(target = "sensor.id", source = "sensorId")
    @Mapping(target = "action", ignore = true)
    // Игнорируем Action, так как его нет в Protobuf.
    ScenarioAction toEntity(DeviceActionProto proto);

    /**
     * Преобразует ScenarioAction в DeviceActionProto.
     */
    default DeviceActionProto toProto(ScenarioAction entity) {
        DeviceActionProto.Builder builder = DeviceActionProto.newBuilder()
                .setSensorId(entity.getSensor().getId())
                .setType(ActionTypeProto.valueOf(entity.getAction().getType().name()));

        if (entity.getAction().getValue() != null) {
            builder.setValue(entity.getAction().getValue());
        }

        return builder.build();
    }

    /**
     * Преобразует ScenarioAction в DeviceActionAvro.
     *
     * @param action объект ScenarioAction.
     * @return объект DeviceActionAvro.
     */
    @Mapping(target = "sensorId", source = "sensor.id")
    @Mapping(target = "type", source = "action.type")
    @Mapping(target = "value", source = "action.value")
    DeviceActionAvro toAvro(ScenarioAction action);
}
