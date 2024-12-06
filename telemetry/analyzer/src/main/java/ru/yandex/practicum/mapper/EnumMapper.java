package ru.yandex.practicum.mapper;

import org.mapstruct.Mapper;
import ru.yandex.practicum.grpc.telemetry.event.ActionTypeProto;
import ru.yandex.practicum.grpc.telemetry.event.ConditionOperationProto;
import ru.yandex.practicum.grpc.telemetry.event.ConditionTypeProto;
import ru.yandex.practicum.model.entity.ActionType;
import ru.yandex.practicum.model.entity.ConditionOperation;
import ru.yandex.practicum.model.entity.ConditionType;

/**
 * Маппер для преобразования enum'ов между JPA и Protobuf.
 */
@Mapper(componentModel = "spring")
public interface EnumMapper {

    // ActionType
    ActionTypeProto map(ActionType type);

    ActionType map(ActionTypeProto proto);

    // ConditionType
    ConditionTypeProto map(ConditionType type);

    ConditionType map(ConditionTypeProto proto);

    // ConditionOperation
    ConditionOperationProto map(ConditionOperation operation);

    ConditionOperation map(ConditionOperationProto proto);
}
