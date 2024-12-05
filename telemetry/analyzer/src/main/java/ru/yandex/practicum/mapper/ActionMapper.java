package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.DeviceActionProto;
import ru.yandex.practicum.model.entity.Action;

/**
 * Маппер для преобразования между Action и DeviceActionProto.
 */
@Mapper(componentModel = "spring")
public interface ActionMapper {

    /**
     * Преобразует DeviceActionProto в Action.
     *
     * @param proto объект DeviceActionProto.
     * @return объект Action.
     */
    @Mapping(target = "id", ignore = true)
    // Игнорируем поле id
    Action toEntity(DeviceActionProto proto);

    /**
     * Преобразует Action в DeviceActionProto.
     *
     * @param action объект Action.
     * @return объект DeviceActionProto.
     */
    default DeviceActionProto toProto(Action action) {
        return DeviceActionProto.newBuilder()
                .setType(ActionTypeProto.valueOf(action.getType().name()))
                .setValue(action.getValue() == null ? 0 : action.getValue())
                .build();
    }
}
